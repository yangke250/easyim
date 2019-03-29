package com.wl.easyim.connect;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
public class Launch {
	
	private static org.slf4j.Logger log=LoggerFactory.getLogger(Launch.class);
	
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(Launch.class);
		log.info("=================================");
		log.info("========easy im connect=========="+System.getenv("spring_profiles_active"));
		log.info("=================================");
		while(true){
			Thread.sleep(Long.MAX_VALUE);
		}
	}
}
