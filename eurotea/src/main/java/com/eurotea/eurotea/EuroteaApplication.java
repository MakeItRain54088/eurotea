package com.eurotea.eurotea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// standard Spring Boot entry point, @SpringBootApplication handles auto-config + component scanning
@SpringBootApplication
public class EuroteaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EuroteaApplication.class, args);
	}

}
