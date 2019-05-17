package com.wl.easyim.biz.service.protocol.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.wl.easyim.biz.api.protocol.c2s.dto.C2sProtocol;
import com.wl.easyim.biz.api.protocol.c2s.enums.C2sCommandType;
import com.wl.easyim.biz.api.protocol.c2s.enums.Result;
import com.wl.easyim.biz.api.protocol.c2s.protocol.PingAck;
import com.wl.easyim.biz.api.protocol.s2s.dto.UserDto;
import com.wl.easyim.biz.service.protocol.IProtocolService;
import com.wl.easyim.route.service.IUserRouteService;

@Service("pingService")
public class PingServiceImpl implements IProtocolService{

	@Resource
	private IUserRouteService userRouteService;
	
	@Override
	public C2sCommandType getC2sCommandType() {
		
		return C2sCommandType.ping;
	}

	@Override
	public C2sProtocol handleProtocol(UserDto userDto, C2sProtocol c2sProtocol, Map<String, String> extendsMap) {
		C2sProtocol c2sProtocolAck = C2sProtocol.builder()
				.type(C2sCommandType.pingAck).uuid(c2sProtocol.getUuid()).build();
		
		PingAck pingAck = new PingAck();
		
		boolean result = userRouteService.pingUserRoute(userDto);
		if(!result){
			pingAck.setResult(Result.pingError);
		}
		
		
		c2sProtocolAck.setBody(JSON.toJSONString(pingAck));
		
		return c2sProtocolAck;
	}

}
