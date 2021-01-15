package com.qai.mvc.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.qai.mvc.dao.ApiDao;
import com.qai.mvc.entity.Message;
import com.qai.mvc.entity.SaveLog;
import com.qai.mvc.entity.SaveLogs;
import com.qai.mvc.entity.SendVO;
import com.qai.mvc.entity.SignUpIdExist;
import com.qai.mvc.entity.UserLogin;
import com.qai.mvc.entity.UserMacAddress;
import com.qai.mvc.entity.UserSignUp;
import com.qai.mvc.session.UserInfo;
import com.qai.mvc.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ApiController {
	//
	
	@Value("$custom.path")
	private String data0;
	
	private final String path = "src/main/resources/";
	
	@Autowired
	private ApiDao apiDao;
	
	@Resource
	private UserInfo userInfo;
	
	@GetMapping("/")
	@ResponseBody
	public ResponseEntity<Message> welcome(HttpServletRequest request) {
		//
		Message message = Message.builder().message("welcome !!.").build();
		HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<Message>(message, headers, HttpStatus.OK);
	}
	
	@GetMapping("/user/getMacAddress")
	@ResponseBody
	public UserMacAddress getMacAddress(HttpServletRequest request) {
		//
		String ip = StringUtil.getIp(request);
		StringBuilder sb = new StringBuilder(ip);
		sb.append("_").append(StringUtil.getUUID(ip));
		
		UserMacAddress mac = UserMacAddress.builder().macAddress(sb.toString()).build();
		return mac;
	}
	
	@PostMapping("/user/signUp")
	@ResponseBody
	public ResponseEntity<Message> signUp(@RequestBody UserSignUp entity, HttpServletRequest request) throws Exception {
		//
		Message message = null;
		
		// 0. call Message Platform
		boolean tf = callAPI(entity);
		//boolean tf = false;
		// 1. make log file
		if(tf) {
			//
			File file = new File("C:/ai/data0/"+entity.getMacAddress()+".log");
			
			for(String v : entity.getLog_text()) {
				//
				FileUtils.writeStringToFile(
					      file, StringUtil.base64(v)+"\n", StandardCharsets.UTF_8, true);
			}
			
			// 2. insert DB
			entity.setIp(StringUtil.getIp(request));
			apiDao.insertUser(entity);
			
			message = Message.builder().message("가입이 완료되었습니다.").build();
			
			HttpHeaders headers= new HttpHeaders();
	        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
	        return new ResponseEntity<>(message, headers, HttpStatus.OK);
			
		}else {
			//
			message = Message.builder().message("Message PlatForm API 호출 실패되었습니다.").build();
			HttpHeaders headers= new HttpHeaders();
	        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
	        return new ResponseEntity<>(message, headers, HttpStatus.BAD_REQUEST);
			
		}
		
	}
	
	@PostMapping("/user/signUpIdExist")
	@ResponseBody
	public ResponseEntity<Message> signUpIdExist(@RequestBody SignUpIdExist entity) {
		//
		List<Object> list = apiDao.findId(entity);
		
		Message message = null;
		if(list.isEmpty()) {
			//
			message = Message.builder().message("사용할 수 있는 ID입니다.").build();
		}else {
			//
			message = Message.builder().message("사용할 수 없는 ID입니다.").build();
		}
		HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
	}
	
	@PostMapping("/user/login")
	@ResponseBody
	public ResponseEntity<Message> login(@RequestBody UserLogin entity) {
		//
		List<Object> list = apiDao.findIdPw(entity);
		
		HttpHeaders headers= new HttpHeaders();
		headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		Message message = null;
		
		// 회원 가입 정보가 없을때
		if(list.isEmpty()) {
			//
			message = Message.builder().message("회원 가입 정보가 없습니다.").build();
	        return new ResponseEntity<>(message, headers, HttpStatus.OK);
		}
		
		Map map = (Map) list.get(0);
		String pw = (String) map.get("user_pw");
		
		if(!pw.equals(entity.getUser_pw())) {
			//비밀번호비교
			message = Message.builder().message("비밀번호가 틀렸습니다.").build();
	        return new ResponseEntity<>(message, headers, HttpStatus.OK);
		}
		
		// success login
		userInfo.setUserId(entity.getUser_id()); //resource
		message = Message.builder().message("로그인 성공.").build();
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
		
	}
	
	@GetMapping("session")
	public String get() {
		return userInfo.toString();
	}
	
	@PostMapping("/gather/insertLog")
	@ResponseBody
	public ResponseEntity<Message> gatherSaveLog(@RequestBody SaveLog entity) throws IOException {
		//
		//FileUtils.touch(new File("/commons/io/"+entity.getMacAddress()+".log"));
		
		//File file = new File(path+"/commons/io/"+entity.getMacAddress()+".log");
		
		File file = new File("C:/ai/data0/"+entity.getMacAddress()+".log");
		
//		FileUtils.writeStringToFile(
//			      file, entity.getMacAddress() +" "+entity.getBase64(), Charset.forName("UTF-8"));
		FileUtils.writeStringToFile(
			      file, StringUtil.base64(entity.getBase64())+"\n", StandardCharsets.UTF_8, true);
		
//		//File file = new File("/commons/io/"+entity.getMacAddress()+".log");
//		final BufferedWriter db = new BufferedWriter(new FileWriter(file));
//		
//		LineIterator it = FileUtils.lineIterator(file, "UTF-8");
//		try {
//        	db.write(entity.getMacAddress() +" "+entity.getBase64());
//		    while (it.hasNext()) {
//		        String line = it.nextLine();
//		        // do something with line
//		        if (line.isEmpty()) {
//					//
//		        	db.write("\n");
//		        	db.write(entity.getMacAddress() +" "+entity.getBase64());
//		        }
//		    }
//		} finally {
//			db.close();
//		    LineIterator.closeQuietly(it);
//		}
//		
		Message message = Message.builder().message("저장 성공.").build();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
	}
	
	@PostMapping("/gather/insertLogs")
	@ResponseBody
	public ResponseEntity<Message> gatherSaveLogs(@RequestBody SaveLogs entity) throws IOException {
		//
		File file = new File("C:/ai/data0/"+entity.getMacAddress()+".log");
		
		for(String v : entity.getLog_text()) {
			//
			FileUtils.writeStringToFile(
				      file, StringUtil.base64(v)+"\n", StandardCharsets.UTF_8, true);
		}
		
		Message message = Message.builder().message("저장 성공.").build();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
	}
	
	@PostMapping("/test/sendMp")
	@ResponseBody
	public void sendMp(@RequestBody UserSignUp entity, HttpServletRequest request) throws Exception {
		//
		callAPI(entity);
	}
	
	
	
	private boolean callAPI(UserSignUp _v) {
		//
		//decoded
		String[] conv = new String[_v.getLog_text().length];
		for(int i = 0 ; i < _v.getLog_text().length ; i++) {
			//
			conv[i] = StringUtil.base64(_v.getLog_text()[i]);
		}
		
		// request body parameters
		SendVO vo = SendVO.builder().uuid(_v.getMacAddress())
				.id(_v.getUser_id())
				.pw(_v.getUser_pw())
				.log_text(conv)
				.build();
		
		// request url
		String url = "http://192.168.0.7:9002/logFiltering";
		log.info("http://192.168.0.7:9002/logFiltering sendData log :: " + vo.toString());
		// Connection Timeout 10초, ReadTimeout 10초 설정.
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout(2*1000);
		factory.setReadTimeout(2*1000);
		
		// create an instance of RestTemplate
		RestTemplate restTemplate = new RestTemplate(factory);

		// create headers
		HttpHeaders headers = new HttpHeaders();
		// set `content-type` header
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		// build the request
		HttpEntity<SendVO> entity = new HttpEntity<>(vo, headers);

		// send POST request
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

		// check response
		if (response.getStatusCode() == HttpStatus.OK) {
		    log.info("Message PlatForm Request Successful !!!!");
		    log.info(response.getBody());
		    return true;
		} else {
			log.info("Message PlatForm Request Failed !!!");
			log.info(response.getStatusCode().toString());
			return false;
		}
        
	}
	///////
}
