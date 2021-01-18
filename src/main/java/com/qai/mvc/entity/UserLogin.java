package com.qai.mvc.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class UserLogin {
	//
	private String macAddress;
	
	private String user_id;
	
	private String user_pw;
	
	private String[] log_text;
}
