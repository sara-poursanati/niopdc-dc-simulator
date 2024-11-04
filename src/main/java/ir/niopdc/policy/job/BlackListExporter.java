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

  @Scheduled(cron = "${app.config.cron.blacklist}")
  @Transactional
  public void runExportTask() {
    log.info("Initializing blackList export");

    String newVersionId = getNextPolicyVersion();
    try {
      BlackList lastRecord = exportBlackList(policyUtils.getBlackListFileName(newVersionId));
      processPolicyVersionUpdate(lastRecord, newVersionId);
    } catch (IOException e) {
      log.error("Failed to export blackLists", e);
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

      BlackListResponse response = BlackListResponse.newBuilder().addAllCardInfos(blackListCardInfos).build();
      FileUtil.createZipFile(filePath, response.toByteArray());

      log.info("Finishing blackList export with {} records", blackListCardInfos.size());
      log.info("lastBlackListRecord = {}", lastBlackListRecord);
    }
    return lastBlackListRecord.get();
  }

  private void processPolicyVersionUpdate(BlackList lastBlackListRecord, String version) {
    Objects.requireNonNull(lastBlackListRecord, "BlackList record cannot be null");

    insertPolicyVersion(lastBlackListRecord, version);
    updatePolicyCurrentVersion(version);
  }

  private void insertPolicyVersion(BlackList lastBlackListRecord, String newVersion) {
    PolicyVersionKey versionKey = PolicyVersionKey.builder()
            .policyId(PolicyEnum.BLACK_LIST.getValue())
            .version(newVersion)
            .build();

    PolicyVersion policyVersion = PolicyVersion.builder()
            .id(versionKey)
            .activationTime(lastBlackListRecord.getInsertionDateTime())
            .releaseTime(lastBlackListRecord.getInsertionDateTime())
            .versionName(policyUtils.getBlackListVersionName(newVersion))
            .build();

    policyVersionService.save(policyVersion);
    log.info("New policy version record inserted with versionName: {}", policyUtils.getBlackListVersionName(newVersion));
  }

  private void updatePolicyCurrentVersion(String version) {
    Policy policy = policyService.findById(PolicyEnum.BLACK_LIST.getValue());
    policy.setCurrentVersion(version);
    policyService.save(policy);
    log.info("Updated currentVersion in Policy table for policyId {}: {}", policy.getId(), version);
  }

  private String getNextPolicyVersion() {
    String currentVersion = policyService.findById(PolicyEnum.BLACK_LIST.getValue()).getCurrentVersion();
    return String.valueOf(Integer.parseInt(currentVersion) + 1);
  }

  private static BlackListCardInfo createBlackListCardInfo(BlackList blackList) {
    return BlackListCardInfo.newBuilder()
        .setCardId(blackList.getCardId())
        .setOperation(OperationEnumMessage.INSERT)
        .build();
  }
}