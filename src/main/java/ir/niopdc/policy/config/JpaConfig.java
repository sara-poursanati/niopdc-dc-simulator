package ir.niopdc.policy.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"ir.niopdc.policy.domain.*"})
@EntityScan({"ir.niopdc.policy.domain.*"})
@EnableTransactionManagement
public class JpaConfig {
}
