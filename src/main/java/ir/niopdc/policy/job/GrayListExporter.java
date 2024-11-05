package ir.niopdc.policy.job;

import ir.niopdc.common.entity.policy.PolicyEnum;
import ir.niopdc.common.grpc.policy.GrayListCardInfo;
import ir.niopdc.common.grpc.policy.GrayListResponse;
import ir.niopdc.common.grpc.policy.OperationEnumMessage;
import ir.niopdc.common.util.FileUtil;
import ir.niopdc.policy.domain.graylist.GrayList;
import ir.niopdc.policy.domain.graylist.GrayListService;
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
public class GrayListExporter {
  private final GrayListService grayListService;
  private final PolicyVersionService policyVersionService;
  private final PolicyService policyService;
  private final PolicyUtils policyUtils;

  @Scheduled(cron = "${app.config.cron.gray-list}")
  @Transactional
  public void runExportTask() {
    log.info("Initializing grayList export");

    String newVersion = getNextPolicyVersion();
    String fileName = policyUtils.getGrayListFileName(newVersion);
    try {
      GrayList lastRecord = exportGrayList(fileName);
      processPolicyVersionUpdate(lastRecord, newVersion);
    } catch (Exception e) {
      log.error("Error occurred after creating file, attempting to delete file: {}", fileName, e);
      deleteFile(fileName);
    }
  }

  private GrayList exportGrayList(String fileName) throws IOException {
    log.info("Starting grayList export");

    AtomicReference<GrayList> lastGrayListRecord = new AtomicReference<>();
    ConcurrentLinkedQueue<GrayListCardInfo> grayListCardInfos = new ConcurrentLinkedQueue<>();

    try (Stream<GrayList> grayListStream = grayListService.streamAll()) {
      grayListStream.forEach(
          grayList -> {
            grayListCardInfos.add(createGrayListCardInfo(grayList));
            lastGrayListRecord.set(grayList);
          });

      if (lastGrayListRecord.get() == null) {
        log.info("No records found, creating an empty file for grayList export");
        FileUtil.createZipFile(fileName, new byte[0]);
      } else {
        GrayListResponse response =
            GrayListResponse.newBuilder().addAllCardInfos(grayListCardInfos).build();
        FileUtil.createZipFile(fileName, response.toByteArray());

        log.info("Finishing grayList export with {} records", grayListCardInfos.size());
        log.info("lastGrayListRecord = {}", lastGrayListRecord);
      }
    }
    return lastGrayListRecord.get() != null ? lastGrayListRecord.get() : new GrayList();
  }

  private void processPolicyVersionUpdate(GrayList lastGrayListRecord, String version) {
    Objects.requireNonNull(lastGrayListRecord, "GrayList record cannot be null");

    insertPolicyVersion(lastGrayListRecord, version);
    updatePolicyCurrentVersion(version);
  }

  private void insertPolicyVersion(GrayList lastRecord, String newVersion) {
    PolicyVersion policyVersion = getPolicyVersion(lastRecord, newVersion);
    policyVersionService.save(policyVersion);
    log.info(
        "New policy version record inserted with versionName: {}", policyVersion.getVersionName());
  }

  private PolicyVersion getPolicyVersion(GrayList lastGrayListRecord, String newVersion) {
    PolicyVersionKey versionKey =
        PolicyVersionKey.builder()
            .policyId(PolicyEnum.GRAY_LIST.getValue())
            .version(newVersion)
            .build();

    return PolicyVersion.builder()
        .id(versionKey)
        .activationTime(lastGrayListRecord.getInsertionDateTime())
        .releaseTime(lastGrayListRecord.getInsertionDateTime())
        .versionName(policyUtils.getGrayListVersionName(newVersion))
        .build();
  }

  private void updatePolicyCurrentVersion(String version) {
    Policy policy = policyService.findById(PolicyEnum.GRAY_LIST.getValue());
    policy.setCurrentVersion(version);
    policyService.save(policy);
    log.info("Updated currentVersion in Policy table for policyId {}: {}", policy.getId(), version);
  }

  private String getNextPolicyVersion() {
    String currentVersion =
        policyService.findById(PolicyEnum.GRAY_LIST.getValue()).getCurrentVersion();
    return currentVersion != null ? String.valueOf(Integer.parseInt(currentVersion) + 1) : "1";
  }

  private static GrayListCardInfo createGrayListCardInfo(GrayList grayList) {
    return GrayListCardInfo.newBuilder()
        .setCardId(grayList.getCardId())
        .setOperation(OperationEnumMessage.INSERT)
        .setType(grayList.getType())
        .setReason(grayList.getReason())
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
