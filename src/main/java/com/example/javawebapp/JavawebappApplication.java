package com.example.javawebapp;

import com.example.javawebapp.model.Bar;
import com.example.javawebapp.repository.BarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class JavawebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavawebappApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(BarRepository barRepository) {
		return args -> {
			Bar bar = new Bar(
					LocalDate.now(),
					"HSI",
					20000.0,
					20001.0,
					19999.0,
					20000.0,
					200000000.0,
					20000.0
			);
			barRepository.insert(bar);
		};
	}

}