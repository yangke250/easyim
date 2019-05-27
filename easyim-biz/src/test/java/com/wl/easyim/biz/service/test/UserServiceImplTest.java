package com.wl.easyim.biz.service.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;

import com.wl.easyim.biz.api.dto.user.UserAuthDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.ResourceType;
import com.wl.easyim.biz.api.service.user.IUserService;
import com.wl.easyim.biz.test.LaunchTest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 用户服务
 * @author wl
 *
 */
public class UserServiceImplTest extends LaunchTest{

	@Resource
	private IUserService userService;
	
	@Test
	public void testUserToken(){
		long tenementId =1;
		String userId ="100";
		
		String token = userService.authEncode(tenementId, userId, ResourceType.pc);
		
		System.out.println(token);
	}
}
