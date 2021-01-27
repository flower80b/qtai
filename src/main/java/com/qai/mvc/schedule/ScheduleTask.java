package com.qai.mvc.schedule;

import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.qai.mvc.entity.SendVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduleTask {
	//
	private static final String domain = "http://192.168.0.7:9002";
	
	// 매일 매시 정각
	@Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul") // 초 분 시 일 월 요일
	//
	//@Scheduled(fixedDelay = 5000) // 초 분 시 일 월 요일
	public void task0() {
		//
		log.info("Schedule start time. :: {}", LocalDateTime.now());
		
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//		factory.setConnectTimeout(300 * 1000);
//		factory.setReadTimeout(300 * 1000);
		
		// create an instance of RestTemplate
		RestTemplate restTemplate = new RestTemplate(factory);

		// create headers
		HttpHeaders headers = new HttpHeaders();
		// set `content-type` header
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		// build the request
		//HttpEntity<SendVO> entity = new HttpEntity<>(vo, headers);

		// send POST request
		restTemplate.postForEntity(this.domain+"/userListLearning", null, null);
		log.info("Schedule end time. :: {}", LocalDateTime.now());
	}
}
