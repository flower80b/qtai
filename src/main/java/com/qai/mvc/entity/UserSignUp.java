package com.qai.mvc.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class UserSignUp {
	//
	private String macAddress;
	private String user_id;
	private String user_pw;
	private String user_pw2;
	private String name;
	private String company;
	private String depart;
	private String position;
	private String cellphone;
	private String email;
	private String address;
	private String addressDetail;
	private String zipCode;
	private String authMethod;
	private String simpleLogin;
	
	private String ip;
	
	private String[] log_text;
}
