package com.easyim.biz.service.c2s.protocol.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.c2s.Ping;
import com.easyim.biz.api.protocol.c2s.PingAck;
import com.easyim.biz.api.protocol.c2s.ReadMessage;
import com.easyim.biz.api.protocol.c2s.ReadMessageAck;
import com.easyim.biz.api.protocol.c2s.ReadMessagePush;
import com.easyim.biz.api.protocol.c2s.ReadMessagePushAck;
import com.easyim.biz.api.protocol.enums.c2s.EasyImC2sType;
import com.easyim.biz.api.service.conversation.IConversationService;
import com.easyim.biz.service.c2s.protocol.IC2SProtocolService;


@Service("readMsgPService")
public class ReadMsgPServiceImpl implements IC2SProtocolService<ReadMessage,ReadMessageAck>{

	@Resource
	private IConversationService conversationService;
	
	@Override
	public EasyImC2sType getType() {
		return EasyImC2sType.readMessage;
	}

	@Override
	public ReadMessageAck handleProtocolBody(UserSessionDto userSessionDto,ReadMessage body,
			Map<String, String> extendsMap) {
		long cid = body.getCid();
		conversationService.cleanUnread(cid);
		
		ReadMessageAck readMessageAck = new ReadMessageAck();
		
		return readMessageAck;
	}

}
