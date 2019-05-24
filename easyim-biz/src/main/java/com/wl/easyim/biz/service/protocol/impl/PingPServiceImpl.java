package com.wl.easyim.biz.service.protocol.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wl.easyim.biz.api.dto.user.UserDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.wl.easyim.biz.api.protocol.enums.c2s.Result;
import com.wl.easyim.biz.api.protocol.protocol.c2s.Ping;
import com.wl.easyim.biz.api.protocol.protocol.c2s.PingAck;
import com.wl.easyim.biz.service.protocol.IC2SProtocolService;
import com.wl.easyim.route.service.IUserRouteService;

@Service("pingPService")
public class PingPServiceImpl implements IC2SProtocolService<Ping, PingAck> {

	@Resource
	private IUserRouteService userRouteService;

	@Override
	public C2sCommandType getC2sCommandType() {

		return C2sCommandType.ping;
	}

	@Override
	public PingAck handleProtocolBody(UserDto userDto, Ping body, Map<String, String> extendsMap) {
		PingAck pingAck = new PingAck();

		boolean result = userRouteService.pingUserRoute(userDto);
		if (!result) {
			pingAck.setResult(Result.pingError);
		}
		return pingAck;
	}

}
