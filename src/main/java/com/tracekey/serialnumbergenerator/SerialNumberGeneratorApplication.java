package com.tracekey.serialnumbergenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SerialNumberGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SerialNumberGeneratorApplication.class, args);
	}

}
