package com.easyim.biz.service.protocol.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.easyim.biz.api.protocol.enums.c2s.Result;
import com.easyim.biz.api.protocol.protocol.c2s.CloseSession;
import com.easyim.biz.api.protocol.protocol.c2s.CloseSessionAck;
import com.easyim.biz.service.protocol.IC2SProtocolService;
import com.easyim.route.service.IUserRouteService;

@Service("closeSessionPService")
public class CloseSessionPServiceImpl implements IC2SProtocolService<CloseSession,CloseSessionAck>{

	@Resource
	private IUserRouteService userRouteService;

	
	@Override
	public C2sCommandType getC2sCommandType() {
		return C2sCommandType.closeSession;
	}

	@Override
	public CloseSessionAck handleProtocolBody(UserSessionDto userSessionDto, CloseSession body, Map<String, String> extendsMap) {
		boolean result = userRouteService.removeUserRoute(userSessionDto);
		
		CloseSessionAck closeSessionAck = new CloseSessionAck();
		if(!result){
			closeSessionAck.setResult(Result.serverError);
		}
		return closeSessionAck;
	}

	

}
