package com.insa.fr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"com.insa.fr"})
public class services_entry_point {

	public static void main(String[] args) {
		SpringApplication.run(services_entry_point.class, args);
	}

}