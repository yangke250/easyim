package com.wl.easyim.biz;

//import javax.validation.Validation;
//import javax.validation.Validator;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
@Slf4j
@MapperScan("com.wl.easyim")
@EnableDubbo
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
	
	@Bean
	public Mapper getMapper(){
		return new DozerBeanMapper();
	}
	
//	@Bean("validator")
//	public Validator getValidator(){
//		Validator validator = 
//				Validation.buildDefaultValidatorFactory().getValidator();
//		return validator;
//	}
}
