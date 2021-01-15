package com.qai.mvc.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.qai.mvc.session.UserInfo;

@Component
public class CertificationInterceptor implements HandlerInterceptor {
	//
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//
//        HttpSession session = request.getSession();
//        UserInfo loginVO = (UserInfo) session.getAttribute("UserInfo");
// 
//        if(ObjectUtils.isEmpty(loginVO)){
//        	//
//            //response.getOutputStream().println("로그인후 사용바람.");
//            return false;
//        	//return new ResponseEntity<>(message, headers, HttpStatus.OK);
//        }else{
//            session.setMaxInactiveInterval(30*60);
//            return true;
//        }
        return true;
    }
 
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub
        
    }
 
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub
        
    }
}
