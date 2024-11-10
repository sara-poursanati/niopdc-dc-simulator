package ir.niopdc.policy.job;

import ir.niopdc.common.entity.policy.PolicyEnum;
import ir.niopdc.common.grpc.policy.*;
import ir.niopdc.common.util.FileUtil;
import ir.niopdc.policy.domain.blacklist.BlackList;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import ir.niopdc.policy.domain.policy.Policy;
import ir.niopdc.policy.domain.policy.PolicyService;
import ir.niopdc.policy.domain.policyversion.PolicyVersion;
import ir.niopdc.policy.domain.policyversion.PolicyVersionKey;
import ir.niopdc.policy.domain.policyversion.PolicyVersionService;
import ir.niopdc.policy.utils.FileUtils;
import ir.niopdc.policy.utils.PolicyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlackListExporter {
  private final BlackListService blackListService;
  private final PolicyVersionService policyVersionService;
  private final PolicyService policyService;
  private final PolicyUtils policyUtils;

  @Scheduled(cron = "${app.config.cron.black-list}")
  @Transactional
  public void runExportTask() {
    log.info("Initializing blackList export");

    String newVersion = getNextPolicyVersion();
    String fileName = policyUtils.getBlackListFileName(newVersion);
    try {
      ZonedDateTime lastQueryDate = exportBlackList(fileName);
      processPolicyVersionUpdate(lastQueryDate, newVersion);
    } catch (Exception e) {
      handleFileCreationError(fileName, e);
    }
  }

  private ZonedDateTime exportBlackList(String fileName) throws IOException {
    log.info("Starting blackList export for file: {}", fileName);

    ConcurrentLinkedQueue<BlackListCardInfo> blackListCardInfos = new ConcurrentLinkedQueue<>();
    ZonedDateTime nowDate = Instant.now().atZone(ZoneId.systemDefault());

    try (Stream<BlackList> blackListStream = blackListService.streamBlackListMinusWhiteListBeforeDate(nowDate)) {
      blackListStream.forEach(blackList -> blackListCardInfos.add(createBlackListCardInfo(blackList)));

      createFileFromBlackList(fileName, blackListCardInfos);
      log.info(
              "Finished blackList export with {} records; last date of query: {}",
              blackListCardInfos.size(),
              nowDate);
    } catch (Exception e) {
      log.error("Error during blackList export stream", e);
      throw e;
    }
    return nowDate;
  }

  private void createFileFromBlackList(
      String fileName, ConcurrentLinkedQueue<BlackListCardInfo> blackListCardInfos)
      throws IOException {
    if (blackListCardInfos.isEmpty()) {
      log.info("No records found, creating an empty file for blackList export");
      FileUtil.createZipFile(fileName, new byte[0]);
    } else {
      BlackListResponse response =
          BlackListResponse.newBuilder().addAllCardInfos(blackListCardInfos).build();
      FileUtil.createZipFile(fileName, response.toByteArray());
    }
  }

  private void processPolicyVersionUpdate(ZonedDateTime lastDate, String version) {
    insertPolicyVersion(lastDate, version);
    updatePolicyCurrentVersion(version);
  }

  private void insertPolicyVersion(ZonedDateTime lastDate, String newVersion) {
    PolicyVersion policyVersion = buildPolicyVersion(lastDate, newVersion);
    policyVersionService.save(policyVersion);
    log.info(
        "New policy version record inserted with versionName: {}", policyVersion.getVersionName());
  }

  private PolicyVersion buildPolicyVersion(ZonedDateTime lastDate, String version) {
    PolicyVersionKey versionKey =
        PolicyVersionKey.builder()
            .policyId(PolicyEnum.BLACK_LIST.getValue())
            .version(version)
            .build();

    return PolicyVersion.builder()
        .id(versionKey)
        .activationTime(lastDate)
        .releaseTime(lastDate)
        .versionName(policyUtils.getBlackListVersionName(version))
        .build();
  }

  private void updatePolicyCurrentVersion(String version) {
    Policy policy = policyService.findById(PolicyEnum.BLACK_LIST.getValue());
    policy.setCurrentVersion(version);
    policyService.save(policy);
    log.info("Updated currentVersion in Policy table for policyId {}: {}", policy.getId(), version);
  }

  private String getNextPolicyVersion() {
    String currentVersion =
        policyService.findById(PolicyEnum.BLACK_LIST.getValue()).getCurrentVersion();
    return currentVersion != null ? String.valueOf(Integer.parseInt(currentVersion) + 1) : "1";
  }

  private static void handleFileCreationError(String fileName, Exception e) {
    log.error("Error occurred after creating file, attempting to delete file: {}", fileName, e);
    FileUtils.deleteFile(fileName);
  }

  private static BlackListCardInfo createBlackListCardInfo(BlackList blackList) {
    return BlackListCardInfo.newBuilder()
        .setCardId(blackList.getCardId())
        .setOperation(OperationEnumMessage.INSERT)
        .build();
  }
}
