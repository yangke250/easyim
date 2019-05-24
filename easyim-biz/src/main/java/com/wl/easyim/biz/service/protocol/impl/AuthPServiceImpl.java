package com.wl.easyim.biz.service.protocol.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.wl.easyim.biz.api.dto.protocol.c2s.C2sProtocol;
import com.wl.easyim.biz.api.dto.protocol.s2s.UserDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.wl.easyim.biz.api.protocol.enums.c2s.Result;
import com.wl.easyim.biz.api.protocol.protocol.c2s.Auth;
import com.wl.easyim.biz.api.protocol.protocol.c2s.AuthAck;
import com.wl.easyim.biz.api.protocol.protocol.c2s.Auth.AuthType;
import com.wl.easyim.biz.bo.UserBo;
import com.wl.easyim.biz.service.protocol.IC2SProtocolService;
import com.wl.easyim.biz.service.user.IUserService;
import com.wl.easyim.route.service.IUserRouteService;

import cn.linkedcare.springboot.redis.template.RedisTemplate;

@Service("authPService")
public class AuthPServiceImpl implements IC2SProtocolService<Auth,AuthAck>{

	@Resource
	private IUserService userService;
	
	@Resource
	private IUserRouteService userRouteService;
	
	@Resource
	private Mapper mapper;
	
	
	@Override
	public C2sCommandType getC2sCommandType() {
		return C2sCommandType.auth;
	}
	
	
	@Override
	public AuthAck handleProtocolBody(UserDto userDto, Auth auth, Map<String, String> extendsMap) {
		String authToken = auth.getAuthToken();
		AuthAck authAck = new AuthAck();
		
		if(StringUtils.isEmpty(authToken)){
			authAck.setResult(Result.authFailed);
			
			return authAck;
		}
		
		
		AuthType authType = auth.getAuthType();
		
		UserBo user = null;
		switch(authType){
			case jwt:
				user = userService.authDecode(auth.getAuthToken());
			break;
			default:
				
		}
		
		userDto.setTenementId(user.getTenementId());
		userDto.setUserId(user.getUserId());
		userDto.setResourceType(user.getResourceType());
		boolean result = userRouteService.addUserRoute(userDto);
		if(!result){
			authAck.setResult(Result.authFailed);
			return authAck;
		}
		
		authAck.setTenementId(user.getTenementId());
		authAck.setUserId(user.getUserId());
		authAck.setResource(user.getResourceType());
		return authAck;
	}

}
