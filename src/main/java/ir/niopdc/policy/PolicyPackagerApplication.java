package ir.niopdc.policy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class PolicyPackagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolicyPackagerApplication.class, args);
	}

}
