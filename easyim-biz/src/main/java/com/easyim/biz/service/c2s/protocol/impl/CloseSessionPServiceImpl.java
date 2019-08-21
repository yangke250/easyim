package com.easyim.biz.service.c2s.protocol.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.c2s.CloseSession;
import com.easyim.biz.api.protocol.c2s.CloseSessionAck;
import com.easyim.biz.api.protocol.enums.c2s.EasyImC2sType;
import com.easyim.biz.api.protocol.enums.c2s.Result;
import com.easyim.biz.service.c2s.protocol.IC2SProtocolService;
import com.easyim.route.service.IUserRouteService;

@Service("closeSessionPService")
public class CloseSessionPServiceImpl implements IC2SProtocolService<CloseSession,CloseSessionAck>{

	@Resource
	private IUserRouteService userRouteService;

	
	@Override
	public EasyImC2sType getType() {
		return EasyImC2sType.closeSession;
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
