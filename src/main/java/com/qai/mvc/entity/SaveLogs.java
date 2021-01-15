package com.qai.mvc.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class SaveLogs {
	//
	private String macAddress;
	
	private String[] log_text;
}
