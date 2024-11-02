package ir.niopdc.policy.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@ConfigurationPropertiesScan
@Getter
@Setter
public class AppConfig {
    private String csvQuote;
    private String csvDelimiter;
    private String csvLineSeparator;
    private int chunkSize;
    private String nationalQuotaPath;
    private String terminalAppPath;
    private String blackListPath;
    private String blackListPrefix;
    private String blackListSuffix;
    private String codingListPath;
    private String grayListPath;
}
