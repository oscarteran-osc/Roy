package com.example.zoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class ZoeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZoeApplication.class, args);
	}

}
