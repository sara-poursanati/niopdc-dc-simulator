package ir.niopdc.simulator.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"ir.niopdc..*"})
@EntityScan(basePackages = "ir.niopdc.simulator.domain")
@EnableTransactionManagement
public class JpaConfig {
}
