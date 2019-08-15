package com.easyim.biz.service.protocol.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.c2s.Ping;
import com.easyim.biz.api.protocol.c2s.PingAck;
import com.easyim.biz.api.protocol.c2s.ReadMessagePush;
import com.easyim.biz.api.protocol.c2s.ReadMessagePushAck;
import com.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.easyim.biz.service.protocol.IC2SProtocolService;


@Service("readMsgPService")
public class ReadMsgPServiceImpl implements IC2SProtocolService<ReadMessagePush,ReadMessagePushAck>{

	@Override
	public C2sCommandType getC2sCommandType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReadMessagePushAck handleProtocolBody(UserSessionDto userSessionDto, ReadMessagePush body,
			Map<String, String> extendsMap) {
		// TODO Auto-generated method stub
		return null;
	}

}
