package ir.niopdc.policy.jobs;

import ir.niopdc.policy.domain.blacklist.BlackList;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlackListExporter {
  private final BlackListService blackListService;

  public static final String HEADER = "card_id\n";
  public static final String FORMAT = "%s\n";

  @Value("${app.blackList.path}")
  private String blackListPath;
  @Value("${app.chunkSize}")
  private int chunkSize;

  @Scheduled(
      fixedDelayString = "${csv.config.fixedDelay}",
      initialDelayString = "${csv.config.initialDelay}")
  public void runCsvExportTask() {
    try {
      log.info("Initializing CSV export");
      exportToCsv(blackListPath);
    } catch (IOException e) {
      log.error("Failed to export CSV", e);
    }
  }

  private void exportToCsv(String filePath) throws IOException {
    long startTime = System.currentTimeMillis();
    log.info("Export started at: {}", startTime);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
      writer.write(HEADER);

      int pageNumber = 0;
      Page<BlackList> page;
      Timestamp timestamp = new Timestamp(0);

      do {
        page = fetchPage(timestamp.toLocalDateTime(), pageNumber);
        String csvContent = convertPageToCsv(page);
        writer.write(csvContent);
        writer.flush();

        log.info("Processed page: {}", pageNumber);
        pageNumber++;

      } while (page.hasNext());

      long endTime = System.currentTimeMillis();
      log.info("Export completed at: {}. Duration: {} ms", endTime, endTime - startTime);
    }
  }

  private Page<BlackList> fetchPage(LocalDateTime timestamp, int pageNumber) {
    return blackListService.fetchPageAfter(
        timestamp, PageRequest.of(pageNumber, chunkSize));
  }

  private static String convertPageToCsv(Page<BlackList> page) {
    return page.getContent().stream().map(BlackListExporter::recordToCsvLine).collect(Collectors.joining());
  }

  private static String recordToCsvLine(BlackList blackList) {
    return String.format(FORMAT, blackList.getCardId());
  }
}
