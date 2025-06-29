package com.example.DACN;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.example.DACN.repository")
@SpringBootApplication
public class DacnApplication {

	public static void main(String[] args) {
		String keyPath = System.getProperty("user.dir") + "/google-vision-api-key.json";
		System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", keyPath);
		SpringApplication.run(DacnApplication.class, args);
	}


}

