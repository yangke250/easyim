package com.wl.easyim.biz.service.protocol.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.wl.easyim.biz.api.dto.protocol.s2s.UserDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.wl.easyim.biz.api.protocol.protocol.c2s.Message;
import com.wl.easyim.biz.api.protocol.protocol.c2s.MessageAck;
import com.wl.easyim.biz.service.protocol.IC2SProtocolService;

@Service("messagePService")
public class MessagePServiceImpl implements IC2SProtocolService<Message,MessageAck>{

	@Override
	public C2sCommandType getC2sCommandType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageAck handleProtocolBody(UserDto userDto, Message body, Map<String, String> extendsMap) {
		// TODO Auto-generated method stub
		return null;
	}






}
