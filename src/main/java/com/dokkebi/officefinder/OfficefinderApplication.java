package com.dokkebi.officefinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Profile;

@Profile({"test", "release", "local"})
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class OfficefinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OfficefinderApplication.class, args);
	}

}
