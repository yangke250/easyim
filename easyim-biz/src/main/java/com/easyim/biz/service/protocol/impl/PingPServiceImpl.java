package com.easyim.biz.service.protocol.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.c2s.Ping;
import com.easyim.biz.api.protocol.c2s.PingAck;
import com.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.easyim.biz.api.protocol.enums.c2s.Result;
import com.easyim.biz.service.protocol.IC2SProtocolService;
import com.easyim.route.service.IUserRouteService;

@Service("messagePushPService")
public class PingPServiceImpl implements IC2SProtocolService<Ping, PingAck> {

	@Resource
	private IUserRouteService userRouteService;

	@Override
	public C2sCommandType getC2sCommandType() {

		return C2sCommandType.ping;
	}

	@Override
	public PingAck handleProtocolBody(UserSessionDto userSessionDto, Ping body, Map<String, String> extendsMap) {
		PingAck pingAck = new PingAck();

		boolean result = userRouteService.pingUserRoute(userSessionDto);
		if (!result) {
			pingAck.setResult(Result.pingError);
		}
		return pingAck;
	}

}
