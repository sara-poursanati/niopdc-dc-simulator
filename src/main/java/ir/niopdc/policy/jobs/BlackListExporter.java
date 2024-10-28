package ir.niopdc.policy.jobs;

import ir.niopdc.common.entity.policy.PolicyEnum;
import ir.niopdc.policy.domain.blacklist.BlackList;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import ir.niopdc.policy.domain.policy.Policy;
import ir.niopdc.policy.domain.policy.PolicyService;
import ir.niopdc.policy.domain.policyversion.PolicyVersion;
import ir.niopdc.policy.domain.policyversion.PolicyVersionKey;
import ir.niopdc.policy.domain.policyversion.PolicyVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlackListExporter {
  private final BlackListService blackListService;
  private final PolicyVersionService policyVersionService;
  private final PolicyService policyService;

  public static final String CSV_HEADER = "card_id,operation_type\n";
  public static final String CSV_FORMAT = "%s,%s\n";
  public static final String OPERATION_TYPE_INSERT = "0";

  @Value("${app.blackList.path}")
  private String blackListPath;

  @Scheduled(
      fixedDelayString = "${csv.config.fixedDelay}",
      initialDelayString = "${csv.config.initialDelay}")
  @Transactional
  public void runCsvExportTask() {
    try {
      log.info("Initializing blackLists CSV export");
      String version = getNextPolicyVersion();
      String fileName = createCsvFileName(version);
      BlackList lastRecord = exportBlackListToCsv(blackListPath + fileName);
      insertPolicyVersion(lastRecord, version);
      updatePolicyCurrentVersion(version);
    } catch (IOException e) {
      log.error("Failed to export blackLists CSV", e);
    }
  }

  private BlackList exportBlackListToCsv(String filePath) throws IOException {
    log.info("Export blackLists progress started");
    AtomicReference<BlackList> lastBlackListRecord = new AtomicReference<>();

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        Stream<BlackList> blackListStream = blackListService.streamAll()) {

      writer.write(CSV_HEADER);
      blackListStream.forEach(
          blackList -> {
            try {
              writer.write(recordToCsvLine(blackList));
              lastBlackListRecord.set(blackList);
            } catch (IOException e) {
              log.error("Failed to export blackList. cause:{}", e.getMessage());
              throw new RuntimeException(e);
            }
          });
      writer.flush();
      log.info("Export blackLists progress finished");
    }
    return lastBlackListRecord.get();
  }

  private void insertPolicyVersion(BlackList lastBlackListRecord, String newVersion) {
    log.info("lastBlackListRecord = {}", lastBlackListRecord);
    Objects.requireNonNull(lastBlackListRecord, "BlackList is null");

    PolicyVersion policyVersion = new PolicyVersion();
    // Setting up the composite key
    PolicyVersionKey versionKey = new PolicyVersionKey();
    versionKey.setPolicyId(PolicyEnum.BLACK_LIST.getValue());
    versionKey.setVersion(newVersion);
    policyVersion.setId(versionKey);

    // Setting the versionName as "BLACK_LIST_<version>"
    String versionName = PolicyEnum.BLACK_LIST.name() + "_" + newVersion;
    policyVersion.setVersionName(versionName);

    // Setting up other fields
    policyVersion.setReleaseTime(ZonedDateTime.of(lastBlackListRecord.getInsertionDateTime().toLocalDateTime(), ZoneId.systemDefault()));
    policyVersion.setActivationTime(ZonedDateTime.of(lastBlackListRecord.getInsertionDateTime().toLocalDateTime(), ZoneId.systemDefault()));

    // Save to the database
    policyVersionService.save(policyVersion);
    log.info("New policy version record inserted with versionName: {}", versionName);
  }

  private void updatePolicyCurrentVersion(String version) {
    Policy policy = policyService.findById(PolicyEnum.BLACK_LIST.getValue());
    policy.setCurrentVersion(version);
    policyService.save(policy);
    log.info("Updated currentVersion in Policy table for policyId {}: {}", policy.getId(), version);
  }

  private String createCsvFileName(String newVersion){
    return PolicyEnum.BLACK_LIST.name() + "_" + newVersion + ".csv";
  }

  private String getNextPolicyVersion() {
    String currentVersion = policyService.findById(PolicyEnum.BLACK_LIST.getValue()).getCurrentVersion();
    int newVersion = Integer.parseInt(currentVersion) + 1;
      return String.valueOf(newVersion);
    }

  private static String recordToCsvLine(BlackList blackList) {
    return String.format(CSV_FORMAT, blackList.getCardId(), OPERATION_TYPE_INSERT);
  }
}
