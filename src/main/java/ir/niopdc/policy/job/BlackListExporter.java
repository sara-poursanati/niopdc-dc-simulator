package ir.niopdc.policy.job;

import ir.niopdc.common.entity.policy.PolicyEnum;
import ir.niopdc.common.grpc.policy.BlackListCardInfo;
import ir.niopdc.common.grpc.policy.BlackListResponse;
import ir.niopdc.common.grpc.policy.OperationEnumMessage;
import ir.niopdc.common.util.FileUtil;
import ir.niopdc.policy.domain.blacklist.BlackList;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import ir.niopdc.policy.domain.policy.Policy;
import ir.niopdc.policy.domain.policy.PolicyService;
import ir.niopdc.policy.domain.policyversion.PolicyVersion;
import ir.niopdc.policy.domain.policyversion.PolicyVersionKey;
import ir.niopdc.policy.domain.policyversion.PolicyVersionService;
import ir.niopdc.policy.utils.PolicyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
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
      BlackList lastRecord = exportBlackList(fileName);
      processPolicyVersionUpdate(lastRecord, newVersion);
    } catch (Exception e) {
      log.error("Error occurred after creating file, attempting to delete file: {}", fileName, e);
      deleteFile(fileName);
    }
  }

  private BlackList exportBlackList(String filePath) throws IOException {
    log.info("Starting blackList export");

    AtomicReference<BlackList> lastBlackListRecord = new AtomicReference<>();
    ConcurrentLinkedQueue<BlackListCardInfo> blackListCardInfos = new ConcurrentLinkedQueue<>();

    try (Stream<BlackList> blackListStream = blackListService.streamAllMinusWhiteList()) {
      blackListStream.forEach(
          blackList -> {
            blackListCardInfos.add(createBlackListCardInfo(blackList));
            lastBlackListRecord.set(blackList);
          });

      BlackListResponse response =
          BlackListResponse.newBuilder().addAllCardInfos(blackListCardInfos).build();
      FileUtil.createZipFile(filePath, response.toByteArray());

      log.info("Finishing blackList export with {} records", blackListCardInfos.size());
      log.info("lastBlackListRecord = {}", lastBlackListRecord);
    }
    return lastBlackListRecord.get();
  }

  private void processPolicyVersionUpdate(BlackList lastBlackListRecord, String newVersion) {
    Objects.requireNonNull(lastBlackListRecord, "BlackList record cannot be null");

    insertPolicyVersion(lastBlackListRecord, newVersion);
    updatePolicyCurrentVersion(newVersion);
  }

  private void insertPolicyVersion(BlackList lastRecord, String newVersion) {
    PolicyVersion policyVersion = getPolicyVersion(lastRecord, newVersion);
    policyVersionService.save(policyVersion);
    log.info(
        "New policy version record inserted with versionName: {}", policyVersion.getVersionName());
  }

  private PolicyVersion getPolicyVersion(BlackList lastBlackListRecord, String version) {
    PolicyVersionKey versionKey =
        PolicyVersionKey.builder()
            .policyId(PolicyEnum.BLACK_LIST.getValue())
            .version(version)
            .build();

    return PolicyVersion.builder()
        .id(versionKey)
        .activationTime(lastBlackListRecord.getInsertionDateTime())
        .releaseTime(lastBlackListRecord.getInsertionDateTime())
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

  private static BlackListCardInfo createBlackListCardInfo(BlackList blackList) {
    return BlackListCardInfo.newBuilder()
        .setCardId(blackList.getCardId())
        .setOperation(OperationEnumMessage.INSERT)
        .build();
  }

  private static void deleteFile(String filePath) {
    Path path = Path.of(filePath);

    if (Files.notExists(path)) {
      log.warn("File deletion skipped; file not found at path: {}", filePath);
      return;
    }

    try {
      Files.delete(path);
      log.info("Successfully deleted file at path: {}", filePath);
    } catch (IOException e) {
      log.error("Failed to delete file at path: {}. Reason: {}", filePath, e.getMessage(), e);
    }
  }
}
