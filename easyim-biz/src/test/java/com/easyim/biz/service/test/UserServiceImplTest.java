package com.easyim.biz.service.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.easyim.biz.api.dto.user.UserAuthDto;
import com.easyim.biz.api.protocol.enums.c2s.ResourceType;
import com.easyim.biz.api.service.user.IUserAuthService;
import com.easyim.biz.test.LaunchTest;

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
	private IUserAuthService userService;
	
	@Test
	public void testUserToken(){
		long tenementId =1;
		String userId ="100";
		
		String token = userService.authEncode(null);
		System.out.println(token);
		UserAuthDto  userAuthDto = userService.authDecode(token);
		
		Assert.assertEquals(tenementId,userAuthDto.getTenementId());
		
		Assert.assertEquals(userId,userAuthDto.getUserId());
		
	}
}
