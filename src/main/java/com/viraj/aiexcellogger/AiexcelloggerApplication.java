package com.viraj.aiexcellogger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AiexcelloggerApplication {

	private static ConfigurableApplicationContext context;

	public static ConfigurableApplicationContext start(String[] args) {
		context = SpringApplication.run(AiexcelloggerApplication.class, args);
		return context;
	}

	public static void main(String[] args) {
		start(args);
	}
}