//package com.qai.mvc.helper;
//
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.type.CollectionType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//public class RestTemplateHelper {
//	//
//	//사용방법
//	//private RestTemplateHelper restTemplateHelper;
//	//UserDto userDto = restTemplateHelper.postForEntity(UserDto.class, "http://localhost:8080/users", userDto);
//	private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateHelper.class);
//
//	private RestTemplate restTemplate;
//	private ObjectMapper objectMapper;
//
//	@Autowired
//	public RestTemplateHelper(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
//		this.restTemplate = restTemplateBuilder.build();
//		this.objectMapper = objectMapper;
//	}
//
//	public <T> T getForEntity(Class<T> clazz, String url, Object... uriVariables) {
//		try {
//			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
//			JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
//			return readValue(response, javaType);
//		} catch (HttpClientErrorException exception) {
//			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
//				LOGGER.info("No data found {}", url);
//			} else {
//				LOGGER.info("rest client exception", exception.getMessage());
//			}
//		}
//		return null;
//	}
//
//	public <T> List<T> getForList(Class<T> clazz, String url, Object... uriVariables) {
//		try {
//			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
//			CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
//			return readValue(response, collectionType);
//		} catch (HttpClientErrorException exception) {
//			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
//				LOGGER.info("No data found {}", url);
//			} else {
//				LOGGER.info("rest client exception", exception.getMessage());
//			}
//		}
//		return null;
//	}
//
//	public <T, R> T postForEntity(Class<T> clazz, String url, R body, Object... uriVariables) {
//		HttpEntity<R> request = new HttpEntity<>(body);
//		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class, uriVariables);
//		JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
//		return readValue(response, javaType);
//	}
//
//	public <T, R> T putForEntity(Class<T> clazz, String url, R body, Object... uriVariables) {
//		HttpEntity<R> request = new HttpEntity<>(body);
//		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class,
//				uriVariables);
//		JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
//		return readValue(response, javaType);
//	}
//
//	public void delete(String url, Object... uriVariables) {
//		try {
//			restTemplate.delete(url, uriVariables);
//		} catch (RestClientException exception) {
//			LOGGER.info(exception.getMessage());
//		}
//	}
//
//	private <T> T readValue(ResponseEntity<String> response, JavaType javaType) {
//		T result = null;
//		if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
//			try {
//				result = objectMapper.readValue(response.getBody(), javaType);
//			} catch (IOException e) {
//				LOGGER.info(e.getMessage());
//			}
//		} else {
//			LOGGER.info("No data found {}", response.getStatusCode());
//		}
//		return result;
//	}
//
//}