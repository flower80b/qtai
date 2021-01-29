package com.qai.mvc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan(basePackageClasses = MvcApplication.class)
@SpringBootApplication
public class MvcApplication {
	//zzzz1111
	public static void main(String[] args) {
		SpringApplication.run(MvcApplication.class, args);
	}

}
