package com.laa66.statlyapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StatlyappApplication {
	public static void main(String[] args) {
		SpringApplication.run(StatlyappApplication.class, args);
	}

}
