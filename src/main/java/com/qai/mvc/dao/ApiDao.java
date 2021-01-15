package com.qai.mvc.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.qai.mvc.entity.SignUpIdExist;
import com.qai.mvc.entity.UserLogin;
import com.qai.mvc.entity.UserSignUp;

@Repository
public class ApiDao {
	//
	protected static final String NAMESPACE = "MainMapper.";

	@Autowired
	private SqlSession sqlSession;

	public String selectName(){
		return sqlSession.selectOne(NAMESPACE + "selectName");
	}
	
	public List<Object> findId(SignUpIdExist entity) {
		//
		Map<String, Object> param = new HashMap<>(); 
		param.put("userId", entity.getId());
		return sqlSession.selectList(NAMESPACE + "findId", param);
	}
	
	public List<Object> findIdPw(UserLogin entity) {
		//
		Map<String, Object> param = new HashMap<>(); 
		param.put("userId", entity.getUser_id());
		param.put("userPw", entity.getUser_pw());
		return sqlSession.selectList(NAMESPACE + "findIdPw", param);
	}
	
	public int insertUser(UserSignUp entity){
		//
		Map<String, Object> param = new HashMap<>(); 
		param.put("userUid", entity.getMacAddress());
		param.put("userId", entity.getUser_id());
		param.put("userPw", entity.getUser_pw());
		param.put("userName", entity.getName());
		param.put("company", entity.getCompany());
		param.put("depart", entity.getDepart());
		param.put("position", entity.getPosition());
		param.put("cellphone", entity.getCellphone());
		param.put("email", entity.getEmail());
		param.put("address", entity.getAddress());
		param.put("addressDetail", entity.getAddressDetail());
		param.put("zipCode", entity.getZipCode());
		param.put("authMethod", entity.getAuthMethod());
		param.put("simpleLogin", entity.getSimpleLogin());
		//param.put("", );
		param.put("regIp", entity.getIp());
		param.put("userYn", "Y");
		
		return sqlSession.insert(NAMESPACE + "insertUser", param); // 1
	}
}

