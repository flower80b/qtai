package com.qai.mvc.schedule;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduleTask {
	//
	@Scheduled(fixedDelay = 2000)
	public void task0() {
		//
		log.info("Schedule test delay {} ms [{}].", 2000, LocalDateTime.now());
	}
}
