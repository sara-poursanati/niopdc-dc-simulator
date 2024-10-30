package ir.niopdc.policy.jobs;

import ir.niopdc.policy.domain.blacklist.BlackList;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlackListExporter {
  private final BlackListService blackListService;

  public static final String CSV_HEADER = "card_id,operation_type\n";
  public static final String CSV_FORMAT = "%s,%s\n";
  public static final String OPERATION_TYPE_INSERT = "0";

  @Value("${app.black-list-path}")
  private String blackListPath;

  @Scheduled(
          fixedDelayString = "${csv.config.fixedDelay}",
          initialDelayString = "${csv.config.initialDelay}")
  @Transactional(readOnly = true)
  public void runCsvExportTask() {
    try {
      log.info("Initializing blackLists CSV export");
      exportToCsv(blackListPath);
    } catch (IOException e) {
      log.error("Failed to export blackLists CSV", e);
    }
  }

  private void exportToCsv(String filePath) throws IOException {
    log.info("Export blackLists progress started at: {}", LocalDateTime.now());
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
         Stream<BlackList> blackListStream = blackListService.streamAll()) {

      writer.write(CSV_HEADER);
      writer.newLine();

      blackListStream.forEach(
              blackList -> {
                try {
                  writer.write(recordToCsvLine(blackList));
                } catch (IOException e) {
                  e.printStackTrace(); // Handle exceptions
                }
              });
    }
    log.info("Export blackLists progress finished at: {}", LocalDateTime.now());
  }


  private static String recordToCsvLine(BlackList blackList) {
    return String.format(CSV_FORMAT, blackList.getCardId(), OPERATION_TYPE_INSERT);
  }
}