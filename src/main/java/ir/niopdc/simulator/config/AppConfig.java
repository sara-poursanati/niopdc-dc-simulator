package ir.niopdc.simulator.config;

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
}
