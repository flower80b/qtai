package com.qai.mvc.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class SendVO {
	//
	private String uuid;
	
	private String id;
	
	private String pw;
	
	private String[] log_text;
		
}
