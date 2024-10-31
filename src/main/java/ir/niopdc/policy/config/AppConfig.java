package ir.niopdc.policy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
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
