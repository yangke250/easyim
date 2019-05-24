package com.wl.easyim.biz.service.protocol.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wl.easyim.biz.api.dto.protocol.s2s.UserDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.wl.easyim.biz.api.protocol.enums.c2s.Result;
import com.wl.easyim.biz.api.protocol.protocol.c2s.CloseSession;
import com.wl.easyim.biz.api.protocol.protocol.c2s.CloseSessionAck;
import com.wl.easyim.biz.service.protocol.IC2SProtocolService;
import com.wl.easyim.route.service.IUserRouteService;

@Service("closeSessionPService")
public class CloseSessionPServiceImpl implements IC2SProtocolService<CloseSession,CloseSessionAck>{

	@Resource
	private IUserRouteService userRouteService;

	
	@Override
	public C2sCommandType getC2sCommandType() {
		return C2sCommandType.closeSession;
	}

	@Override
	public CloseSessionAck handleProtocolBody(UserDto userDto, CloseSession body, Map<String, String> extendsMap) {
		boolean result = userRouteService.removeUserRoute(userDto);
		
		CloseSessionAck closeSessionAck = new CloseSessionAck();
		if(!result){
			closeSessionAck.setResult(Result.serverError);
		}
		return closeSessionAck;
	}

	

}
