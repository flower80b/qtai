package com.qai.mvc.util;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.codec.binary.Base64;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtil {
	//
	public static String getIp(HttpServletRequest request) {
		//
		String ip = request.getHeader("X-Forwarded-For");
		log.info(">>>> X-FORWARDED-FOR : " + ip);
 
		if (ip == null) {
		    ip = request.getHeader("Proxy-Client-IP");
		    log.info(">>>> Proxy-Client-IP : " + ip);
		}
		if (ip == null) {
		    ip = request.getHeader("WL-Proxy-Client-IP"); // 웹로직
		    log.info(">>>> WL-Proxy-Client-IP : " + ip);
		}
		if (ip == null) {
		    ip = request.getHeader("HTTP_CLIENT_IP");
		    log.info(">>>> HTTP_CLIENT_IP : " + ip);
		}
		if (ip == null) {
		    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		    log.info(">>>> HTTP_X_FORWARDED_FOR : " + ip);
		}
		if (ip == null) {
		    ip = request.getRemoteAddr();
		}
		
		log.info(">>>> Result : IP Address : "+ip);
		return ip;
	}
	
	public static String getUUID(String ip) {
		//
		//return UUID.nameUUIDFromBytes(ip.getBytes()).toString();
		return UUID.randomUUID().toString();
	}
	
	public static String base64(String strEncoded) {
		//
		//String text = "ktko"; /* base64 encoding */ 
		//byte[] encodedBytes = Base64.encodeBase64(v.getBytes()); 
		/* base64 decoding */ 
		byte[] decodedBytes = Base64.decodeBase64(strEncoded.getBytes()); 
		log.info("인코딩 전 : " + strEncoded); 
		//log.info("인코딩 text : " + new String(encodedBytes)); 
		log.info("디코딩 text : " + new String(decodedBytes));
		return new String(decodedBytes);
	}
}
