package ir.niopdc.policy.jobs;

import ir.niopdc.policy.domain.blacklist.BlackList;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
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

  public static final String CSV_HEADER = "card_id\n";
  public static final String CSV_FORMAT = "%s\n";

  @Value("${app.blackList.path}")
  private String blackListPath;
  @Value("${app.chunkSize}")
  private int chunkSize;

  @Scheduled(
      fixedDelayString = "${csv.config.fixedDelay}",
      initialDelayString = "${csv.config.initialDelay}")
  public void runCsvExportTask() {
    try {
      log.info("Initializing blackLists CSV export");
      exportToCsv(blackListPath);
    } catch (IOException e) {
      log.error("Failed to export blackLists CSV", e);
    }
  }

  private void exportToCsv(String filePath) throws IOException {
    long startTime = System.currentTimeMillis();
    log.info("blackLists CSV Export started at: {}", startTime);

    int pageNumber = 0;
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
      writer.write(CSV_HEADER);

      Page<BlackList> page;
      Timestamp timestamp = new Timestamp(0);

      do {
        page = fetchPage(timestamp.toLocalDateTime(), pageNumber);
        String csvContent = convertPageToCsv(page);
        writer.write(csvContent);
        log.info("Processed page: {}. at: {}", pageNumber, System.currentTimeMillis());
        pageNumber++;

      } while (page.hasNext());

      long endTime = System.currentTimeMillis();
      log.info("Export completed at: {}. Duration: {} ms", endTime, endTime - startTime);

    } catch (IOException e) {
      log.error(
          "Failed to export CSV to file path: {}, error occurred on page number: {}",
          filePath,
          pageNumber,
          e);
      throw e;
    }
  }

  private Page<BlackList> fetchPage(LocalDateTime timestamp, int pageNumber) {
    try {
      return blackListService.fetchPageAfter(timestamp, PageRequest.of(pageNumber, chunkSize));
    } catch (DataAccessException ex) {
      log.error("Failed to fetch data for page {}: {}", pageNumber, ex.getMessage());
      throw ex;
    }
  }

  private static String convertPageToCsv(Page<BlackList> page) {
    return page.getContent().stream()
        .map(BlackListExporter::recordToCsvLine)
        .collect(Collectors.joining());
  }

  private static String recordToCsvLine(BlackList blackList) {
    return String.format(CSV_FORMAT, blackList.getCardId());
  }
}
