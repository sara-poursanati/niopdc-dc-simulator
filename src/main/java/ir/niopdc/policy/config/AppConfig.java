package ir.niopdc.policy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = {"ir.niopdc.*"})
@EnableAspectJAutoProxy
public class AppConfig {

    @Bean
    public ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ObjectWriter jsonWriter() {
        return jsonMapper().writer().withDefaultPrettyPrinter();
    }
}
