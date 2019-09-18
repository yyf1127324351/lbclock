package com.lb.lbclock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LbclockApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(LbclockApplication.class);
		springApplication.run(LbclockApplication.class, args);
	}

}
