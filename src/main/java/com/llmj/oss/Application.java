package com.llmj.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableAsync
@Slf4j(topic = "ossLogger")
//@ComponentScan(basePackages = "com.llmj")//注解扫描器 
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		log.info("Oss_server start ----->>>>>>>>>>>>>>>>>>>>>>>>> ");
	}
	
}
