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
import org.springframework.boot.configurationprocessor.json.JSONObject;
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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
import com.qai.mvc.share.ShareVO;
import com.qai.mvc.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
//@CrossOrigin(origins = "http://localhosrt", allowedHeaders = "*")
//@CrossOrigin(origins = {"http://localhost:9000", "http://qtai.duckdns.org:9000" }, allowedHeaders = "*")
//@CrossOrigin(origins = "http://qtai.duckdns.org:9000, withCredentials = true" )
//@CrossOrigin("*")
public class ApiController {
	//
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
	public ResponseEntity<Message> signUp(@RequestBody UserSignUp entity, HttpServletRequest request) {
		//
		// init
		Message responseBody = null;
		HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        
		// 0. call Message Platform
        Message tf = this.callMessagePlatform(ShareVO.mpdomain+"/logFiltering", entity.getLog_text(), entity.getUser_id(), entity.getUser_pw(), entity.getMacAddress());
		// f, s
        if("F".equals(tf.getStatus())) {
			//
			responseBody = Message.builder().message("Message PlatForm API 호출 실패되었습니다.").status("F").build();
	        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
		}
		
		// 1. make log file
		boolean rtn = this.makeLogFile(entity.getLog_text(), "P1", entity.getMacAddress());
		if(!rtn) {
			//
			responseBody = Message.builder().message("Log File 생성 실패되었습니다.").status("F").build();
	        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
		}
		
		// 2. insert DB
		entity.setIp(StringUtil.getIp(request));
		apiDao.insertUser(entity);
		responseBody = Message.builder().message("가입이 완료되었습니다.").status("S").build();
		return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
		
	}
	
	@PostMapping("/user/signUpIdExist")
	@ResponseBody
	public ResponseEntity<Message> signUpIdExist(@RequestBody SignUpIdExist entity) {
		//
		List<Object> list = apiDao.findId(entity);
		
		Message responseBody = null;
		if(list.isEmpty()) {
			//
			responseBody = Message.builder().message("사용할 수 있는 ID입니다.").status("S").build();
		}else {
			//
			responseBody = Message.builder().message("사용할 수 없는 ID입니다.").status("F").build();
		}
		HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
	}
	
	@PostMapping("/user/login")
	@ResponseBody
	public ResponseEntity<Message> login(@RequestBody UserLogin entity) {
		//
		HttpHeaders headers= new HttpHeaders();
		headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		// 0. 로그인 체크 ==================================================================
		Message responseBody = this.loginDBCheck(entity);
		if(!"S".equals(responseBody.getStatus())) {
			//
			return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
		}
		
		// 1. call API Message Platform ========================================================================================
		Message tf = this.callMessagePlatform(ShareVO.mpdomain+"/loginLogFiltering", entity.getLog_text(), entity.getUser_id(), entity.getUser_pw(), entity.getMacAddress());
		
		// F, N, S
		if(!"S".equals(tf.getStatus())) {
			//
			return new ResponseEntity<>(tf, headers, HttpStatus.OK);
		}
		
		// 2. make file ================================================================
		boolean rtn = this.makeLogFile(entity.getLog_text(), "P2", entity.getMacAddress());
		// log fail
		if(!rtn) {
			//
			responseBody = Message.builder().message("Log File 생성 실패 되었습니다.").status("F").build();
	        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
		}
		
		responseBody = tf;
		return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
	}
	
	@PostMapping("/user/auth")
	@ResponseBody
	public ResponseEntity<Message> loginAuth(@RequestBody UserLogin entity) {
		//
		HttpHeaders headers= new HttpHeaders();
		headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		Message responseBody = Message.builder().message("로그인 성공.").status("S").build();
		
		// 0. auth 체크 ==================================================================
		if(!"dhsruf".equals(entity.getAuth()) ) {
			//
			Message ms = Message.builder().message("개인 인증 메세지가 일치 하지 않습니다.").status("F").build();
			return new ResponseEntity<>(ms, headers, HttpStatus.OK);
		}
		
		// 1. call API Message Platform ========================================================================================
		//authFiltering
		Message responseAPI = this.callMessagePlatform(ShareVO.mpdomain+"/authFiltering", entity.getLog_text(), entity.getUser_id(), entity.getUser_pw(), entity.getMacAddress());
		
		// F, N, S
		if(!"S".equals(responseAPI.getStatus())) {
			//
			return new ResponseEntity<>(responseAPI, headers, HttpStatus.OK);
		}
		
		// 2. make file ================================================================
		boolean rtn = this.makeLogFile(entity.getLog_text(), "P2", entity.getMacAddress());
		// log fail
		if(!rtn) {
			//
			responseBody = Message.builder().message("Log File 생성 실패 되었습니다.").status("F").build();
	        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
		}
		
		responseBody = Message.builder().message("로그인 성공.").status("S").build();
		responseBody = responseAPI;
		return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
	}
	
	
//	@GetMapping("session")
//	public String get() {
//		return userInfo.toString();
//	}
	
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
		//callAPI(entity);
		this.callMessagePlatform(ShareVO.mpdomain+"/logFiltering", entity.getLog_text(), entity.getUser_id(), entity.getUser_pw(), entity.getMacAddress());
	}
	
	private Message callMessagePlatform(String url, String[] text, String id, String pw, String mac) {
		//
		try {
			//
			//decoded
			String[] conv = new String[text.length];
			for (int i = 0; i < text.length; i++) {
				//
				conv[i] = StringUtil.base64(text[i]);
			}

			// request body parameters
			SendVO vo = SendVO.builder().uuid(mac).id(id).pw(pw).log_text(conv).build();

			// request url
			log.info(url + " sendData log :: " + vo.toString());
			// Connection Timeout 10초, ReadTimeout 10초 설정.
			HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
			factory.setConnectTimeout(300 * 1000);
			factory.setReadTimeout(300 * 1000);

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
			ResponseEntity<String> responseString = restTemplate.postForEntity(url, entity, String.class);
			
			// F, N, S
			// check response
			if (responseString.getStatusCode() == HttpStatus.OK) {
				//
				log.info("Message PlatForm Request Successful !!!!");
				Message ms = new Gson().fromJson(responseString.getBody(), Message.class);
				// 로그인시
				if(url.equals(ShareVO.mpdomain+"/loginLogFiltering")) {
					//
					if("F".equals(ms.getStatus())) {
						//
						
						ms.setMessage("패턴인식 불일치로 로그인실패 입니다. (패턴 인식율 : "+ String.format("%.2f", Double.valueOf(ms.getMessage()) *100) +" 점)");
					}else if("N".equals(ms.getStatus())) {
						//
						ms.setMessage("패턴인식 확인필요. (패턴 인식율 : "+ String.format("%.2f", Double.valueOf(ms.getMessage()) *100) +" 점) \n 개인확인 메시지를 입력하세요.");
					}else {
						//
						ms.setMessage("로그인 성공 입니다. (패턴 인식율 : "+ String.format("%.2f", Double.valueOf(ms.getMessage()) *100) +" 점)");
					}
					
//					if("teamtpzoo".equals(id)) {
//						//
//						//String a = ms.getMessage().substring(0, 4);
//						//int b = (int) (Double.valueOf(a) *100);
//						ms.setMessage("패턴인식 확인필요. (패턴 인식율 : "+ 50 +" 점) \n 개인확인 메시지를 입력하세요.");
//						ms.setStatus("N");
//					}
					
				}else if(url.equals(ShareVO.mpdomain+"/authFiltering")) {
					// 권한체크시
					ms.setMessage("로그인 성공.");
					ms.setStatus("S");
				}else {
					//사용자 등록시
					ms.setMessage("사용자 등록 완료.");
					ms.setStatus("S");
				}
				return ms;
			} else {
				log.info("Message PlatForm Request Failed !!!");
				return Message.builder().message("Message PlatForm Failed !!!").status("F").build();
			}
		}catch(Exception e) {
			//
			//e.printStackTrace();
			log.error("Exception !!!!! "+e.getMessage());
			return Message.builder().message("Message PlatForm Exception !! "+e.getMessage()).status("F").build();
		}
		
	}
	
	private boolean makeLogFile(String[] text, String prefix, String mac) {
		//
		try {
			//
			File file = new File(ShareVO.filePath + prefix + "_" + mac + ".log");

			for (String v : text) {
				//
				FileUtils.writeStringToFile(file, StringUtil.base64(v) + "\n", StandardCharsets.UTF_8, true);
			}
			return true;
		}catch(Exception e) {
			//
			log.error("Exception !!!!! "+e.getMessage());
			return false;
		}
	}
	
	private Message loginDBCheck(UserLogin entity) {
		//
		List<Object> list = apiDao.findIdPw(entity);
		
//		UsersDoc doc = usersRepo.findByUserId(userId)
//				.filter((data) -> data.getPasswd().equals(passwd))
//				.orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "Check your infomation."));
		
		Message message = null;
		
		// 회원 가입 정보가 없을때
		if(list.isEmpty()) {
			//
			message = Message.builder().message("회원 가입 정보가 없습니다.").status("F").build();
			return message;
		}
		
		Map map = (Map) list.get(0);
		String pw = (String) map.get("user_pw");
		
		if(!pw.equals(entity.getUser_pw())) {
			//비밀번호비교
			message = Message.builder().message("비밀번호가 틀렸습니다.").status("F").build();
			return message;
		}
		
		// success login
		userInfo.setUserId(entity.getUser_id()); //resource
		message = Message.builder().message("회원가입 성공").status("S").build();
		return message;
	}
}
