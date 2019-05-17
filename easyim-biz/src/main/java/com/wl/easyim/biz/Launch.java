package com.wl.easyim.biz;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
@Slf4j
public class Launch {
	
	
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(Launch.class);
		log.info("=================================");
		log.info("==========easy im biz============"+System.getenv("spring_profiles_active"));
		log.info("=================================");
		while(true){
			Thread.sleep(Long.MAX_VALUE);
		}
	}
	
	public Mapper getMapper(){
		return new DozerBeanMapper();
	}
}
