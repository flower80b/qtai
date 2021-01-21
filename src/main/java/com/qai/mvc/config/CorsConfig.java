package com.qai.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                .addMapping("/**")
                //.allowedOrigins("http://localhost:8080", "http://localhost:8081");
                .allowedOrigins("http://qtai.duckdns.org:9000")
                .allowCredentials(true); // 필요한 경우
            }
        };
    }
}
