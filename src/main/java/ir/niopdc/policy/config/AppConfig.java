package ir.niopdc.policy.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = {"ir.niopdc.*"})
@EnableAspectJAutoProxy
public class AppConfig {
}
