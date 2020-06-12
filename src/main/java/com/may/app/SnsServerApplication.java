package com.may.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
//@EntityScan(basePackages = "com.may.app")
public class SnsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnsServerApplication.class, args);
	}
}
