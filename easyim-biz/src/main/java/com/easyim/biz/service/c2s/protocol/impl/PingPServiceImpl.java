package com.easyim.biz.service.c2s.protocol.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.c2s.Ping;
import com.easyim.biz.api.protocol.c2s.PingAck;
import com.easyim.biz.api.protocol.enums.c2s.EasyImC2sType;
import com.easyim.biz.api.protocol.enums.c2s.Result;
import com.easyim.biz.service.c2s.protocol.IC2SProtocolService;
import com.easyim.route.service.IUserRouteService;

@Service("messagePushPService")
public class PingPServiceImpl implements IC2SProtocolService<Ping, PingAck> {

	@Resource
	private IUserRouteService userRouteService;

	@Override
	public EasyImC2sType getType() {

		return EasyImC2sType.ping;
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
