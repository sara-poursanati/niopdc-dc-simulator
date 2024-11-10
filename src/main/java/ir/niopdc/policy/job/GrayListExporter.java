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
        ZonedDateTime lastDateQuery = exportGrayList(fileName);
        processPolicyVersionUpdate(lastDateQuery, newVersion);
      } catch (Exception e) {
        handleFileCreationError(fileName, e);
      }
    }

  private ZonedDateTime exportGrayList(String fileName) throws IOException {
    log.info("Starting grayList export for file: {}", fileName);

    ConcurrentLinkedQueue<GrayListCardInfo> grayListCardInfos = new ConcurrentLinkedQueue<>();
    ZonedDateTime nowDate = Instant.now().atZone(ZoneId.systemDefault());

    try (Stream<GrayList> grayListStream = grayListService.streamAllBeforeDate(nowDate)) {
      grayListStream.forEach(grayList -> grayListCardInfos.add(createGrayListCardInfo(grayList)));

      createFileFromGrayList(fileName, grayListCardInfos);
      log.info(
          "Finished grayList export with {} records; last date of query: {}",
          grayListCardInfos.size(),
          nowDate);

    } catch (Exception e) {
      log.error("Error during grayList export stream", e);
      throw e;
    }
    return nowDate;
  }

  private void createFileFromGrayList(
      String fileName, ConcurrentLinkedQueue<GrayListCardInfo> grayListCardInfos)
      throws IOException {
    if (grayListCardInfos.isEmpty()) {
      log.info("No records found, creating an empty file for grayList export");
      FileUtil.createZipFile(fileName, new byte[0]);
    } else {
      GrayListResponse response =
          GrayListResponse.newBuilder().addAllCardInfos(grayListCardInfos).build();
      FileUtil.createZipFile(fileName, response.toByteArray());
    }
  }

  private void processPolicyVersionUpdate(ZonedDateTime lastDate, String version) {
    insertPolicyVersion(lastDate, version);
    updatePolicyCurrentVersion(version);
  }

  private void insertPolicyVersion(ZonedDateTime lastDate, String version) {
    PolicyVersion policyVersion = buildPolicyVersion(lastDate, version);
    policyVersionService.save(policyVersion);
    log.info(
        "New policy version record inserted with versionName: {}", policyVersion.getVersionName());
  }

  private PolicyVersion buildPolicyVersion(ZonedDateTime lastDate, String version) {
    PolicyVersionKey versionKey =
        PolicyVersionKey.builder()
            .policyId(PolicyEnum.GRAY_LIST.getValue())
            .version(version)
            .build();

    return PolicyVersion.builder()
        .id(versionKey)
        .activationTime(lastDate)
        .releaseTime(lastDate)
        .versionName(policyUtils.getGrayListVersionName(version))
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

  private static void handleFileCreationError(String fileName, Exception e) {
    log.error("Error occurred after creating file, attempting to delete file: {}", fileName, e);
    FileUtils.deleteFile(fileName);
  }

  private static GrayListCardInfo createGrayListCardInfo(GrayList grayList) {
    return GrayListCardInfo.newBuilder()
        .setCardId(grayList.getCardId())
        .setOperation(OperationEnumMessage.INSERT)
        .setType(grayList.getType())
        .setReason(grayList.getReason())
        .build();
  }
}
