package com.easyim.connect;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

import lombok.extern.slf4j.Slf4j;


//@SpringBootApplication
@Slf4j
@EnableDubbo
@ComponentScan
@Configuration
@PropertySource(
value = {
		"classpath:application-easyim-connect.properties",
		"classpath:application-easyim-connect-${spring.profiles.active}.properties"},
ignoreResourceNotFound = true, encoding = "UTF-8")
public class Launch {
	
	
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
