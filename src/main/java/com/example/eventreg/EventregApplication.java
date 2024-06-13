package com.example.eventreg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.*")
public class EventregApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventregApplication.class, args);
	}

}
