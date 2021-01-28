package com.qai.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.qai.mvc.share.ShareVO;

@Configuration
public class CorsConfig {
	//
	@Bean
    public WebMvcConfigurer corsConfigurer() {
		//
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                //.addMapping("/somePath/**")
                .addMapping("/**")
                //.allowedOrigins("http://localhost:8080", "http://localhost:8081");
                //.allowedMethods("GET", "POST"); //메소드별 정의도 가능
                .allowedOrigins(ShareVO.maindomain)
                .allowCredentials(true); // cors사용시 session이 필요한 경우
            }
        };
    }
}
