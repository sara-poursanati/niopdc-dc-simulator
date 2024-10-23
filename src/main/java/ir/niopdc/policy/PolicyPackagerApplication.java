package ir.niopdc.policy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"ir.niopdc.*"})
@EnableAspectJAutoProxy
@EnableScheduling
public class PolicyPackagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolicyPackagerApplication.class, args);
	}

}
