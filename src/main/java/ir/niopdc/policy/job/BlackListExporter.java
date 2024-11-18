package ir.niopdc.policy.job;

import ir.niopdc.common.entity.policy.PolicyEnum;
import ir.niopdc.common.grpc.policy.*;
import ir.niopdc.common.util.FileUtil;
import ir.niopdc.domain.blacklist.BlackList;
import ir.niopdc.domain.blacklist.BlackListService;
import ir.niopdc.domain.policy.Policy;
import ir.niopdc.domain.policy.PolicyService;
import ir.niopdc.domain.policyversion.PolicyVersion;
import ir.niopdc.domain.policyversion.PolicyVersionKey;
import ir.niopdc.domain.policyversion.PolicyVersionService;
import ir.niopdc.policy.utils.PolicyUtils;
import ir.niopdc.policy.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
      String checksum = SecurityUtils.createSHA512Hash(fileName);
      updatePolicyVersion(lastQueryDate, newVersion, checksum);
    } catch (Exception e) {
      handleFileCreationError(fileName, e);
    }
  }

  private ZonedDateTime exportBlackList(String fileName) throws IOException {
    List<BlackListCardInfo> blackListCardInfos = new ArrayList<>();
    ZonedDateTime currentDate = Instant.now().atZone(ZoneId.systemDefault());

    try (Stream<BlackList> blackListStream =
        blackListService.streamBlackListMinusWhiteListBeforeDate(currentDate)) {

      blackListStream.map(this::createBlackListCardInfo).forEach(blackListCardInfos::add);
      createFileFromBlackList(fileName, blackListCardInfos);

      log.info(
          "Finished blackList export with {} records; last date of query: {}",
          blackListCardInfos.size(),
          currentDate);
    }
    return currentDate;
  }

  private void createFileFromBlackList(String fileName, List<BlackListCardInfo> blackListCardInfos)
      throws IOException {
      BlackListResponse response =
          BlackListResponse.newBuilder().addAllCardInfos(blackListCardInfos).build();
      FileUtil.createZipFile(fileName, response.toByteArray());
  }

  private void updatePolicyVersion(ZonedDateTime lastDate, String version, String checksum) {
    insertPolicyVersion(lastDate, version, checksum);
    updateCurrentPolicyVersion(version);
  }

  private void insertPolicyVersion(ZonedDateTime lastDate, String newVersion, String checksum) {
    PolicyVersion policyVersion = buildPolicyVersion(lastDate, newVersion, checksum);
    policyVersionService.save(policyVersion);
    log.info(
        "New policy version record inserted with versionName: {}", policyVersion.getVersionName());
  }

  private PolicyVersion buildPolicyVersion(
      ZonedDateTime lastDate, String version, String checksum) {
    PolicyVersionKey versionKey =
        PolicyVersionKey.builder()
            .policyId(PolicyEnum.BLACK_LIST.getValue())
            .version(version)
            .build();

    return PolicyVersion.builder()
        .id(versionKey)
        .versionName(policyUtils.getBlackListVersionName(version))
        .checksum(checksum)
        .activationTime(lastDate)
        .releaseTime(lastDate)
        .build();
  }

  private void updateCurrentPolicyVersion(String version) {
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

  private BlackListCardInfo createBlackListCardInfo(BlackList blackList) {
    return BlackListCardInfo.newBuilder()
        .setCardId(blackList.getCardId())
        .setOperation(OperationEnumMessage.INSERT)
        .build();
  }

  private static void handleFileCreationError(String fileName, Exception e) {
    log.error("Error occurred after creating file, attempting to delete file: {}", fileName, e);
    FileUtils.deleteQuietly(new File(fileName));
  }
}
