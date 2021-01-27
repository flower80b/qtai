package com.qai.mvc.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
//@NoArgsConstructor
public class Message {
	//
	private Number code;
	private String message;
	private String status;
}
