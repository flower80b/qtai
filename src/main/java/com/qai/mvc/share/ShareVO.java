package com.qai.mvc.share;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Data;

@Component
public class ShareVO {
	//
    public static String filePath;

    public static String mpdomain;

    public static String maindomain;

    
    @Value("${custom.filePath}")
    public void setFilePath(String value) {
    	filePath = value;
    }
    
    @Value("${custom.mpdomain}")
    public void setMpDomain(String value) {
    	mpdomain = value;
    }
    
    @Value("${custom.maindomain}")
    public void setMainDomain(String value) {
    	maindomain = value;
    }
}
