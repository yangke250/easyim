package com.easyim.biz.service.c2s.protocol.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.easyim.biz.api.dto.protocol.C2sProtocol;
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
import com.easyim.route.service.IProtocolRouteService;


@Service("readMsgPService")
public class ReadMsgPServiceImpl implements IC2SProtocolService<ReadMessage,ReadMessageAck>{

	@Resource
	private IConversationService conversationService;
	
	@Resource
	private IProtocolRouteService routeService;
	
	@Override
	public EasyImC2sType getType() {
		return EasyImC2sType.readMessage;
	}

	@Override
	public ReadMessageAck handleProtocolBody(UserSessionDto userSessionDto,ReadMessage body,
			Map<String, String> extendsMap) {
		long cid = body.getCid();
		long msgId = body.getMsgId();
		long tenementId = userSessionDto.getTenementId();
		String userId = userSessionDto.getUserId();
		
		conversationService.cleanUnread(userId, cid);
		
		
	
		C2sProtocol c2sProtocol = new C2sProtocol();
		
		c2sProtocol.setType(EasyImC2sType.readMessage);
		c2sProtocol.setBody(JSON.toJSONString(c2sProtocol));
		
		
		this.routeService.route(tenementId, userId,JSON.toJSONString(c2sProtocol));
		
		return new ReadMessageAck();
	}

}
