package com.wl.easyim.biz.service.protocol.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.wl.easyim.biz.api.protocol.c2s.dto.C2sProtocol;
import com.wl.easyim.biz.api.protocol.c2s.enums.C2sCommandType;
import com.wl.easyim.biz.api.protocol.c2s.enums.Result;
import com.wl.easyim.biz.api.protocol.c2s.protocol.Auth;
import com.wl.easyim.biz.api.protocol.c2s.protocol.Auth.AuthType;
import com.wl.easyim.biz.api.protocol.s2s.dto.UserDto;
import com.wl.easyim.biz.api.protocol.c2s.protocol.AuthAck;
import com.wl.easyim.biz.bo.UserBo;
import com.wl.easyim.biz.service.protocol.IProtocolService;
import com.wl.easyim.biz.service.user.IUserService;
import com.wl.easyim.route.service.IUserRouteService;

import cn.linkedcare.springboot.redis.template.RedisTemplate;

@Service("authService")
public class AuthServiceImpl implements IProtocolService{

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
	
	private C2sProtocol createC2sProtocolAck(String uuid,AuthAck authAck){
		C2sProtocol c2sProtocol = new C2sProtocol();
		c2sProtocol.setType(C2sCommandType.authAck);
		c2sProtocol.setUuid(uuid);
		c2sProtocol.setBody(JSON.toJSONString(authAck));
		
		return c2sProtocol;
	}

	@Override
	public C2sProtocol handleProtocol(UserDto userDto,C2sProtocol c2sProtocol,Map<String,String> extendsMap) {
		String uuid =  c2sProtocol.getUuid();
		
		String body =  c2sProtocol.getBody();
		
		Auth auth  = JSON.parseObject(body,Auth.class);
		
		String authToken = auth.getAuthToken();
		if(StringUtils.isEmpty(authToken)){
			AuthAck authAck = new AuthAck();
			authAck.setResult(Result.authFailed);
			
			return createC2sProtocolAck(uuid,authAck);
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
		userRouteService.addUserRoute(userDto);
		
		AuthAck authAck = new AuthAck();
		authAck.setTenementId(user.getTenementId());
		authAck.setUserId(user.getUserId());
		authAck.setResource(user.getResourceType());
		
		return createC2sProtocolAck(uuid,authAck);
	}

}
