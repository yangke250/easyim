package com.easyim.biz.service.c2s.protocol.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.dozer.Mapper;
import org.springframework.stereotype.Service;

import com.easyim.biz.api.dto.message.SendMsgDto;
import com.easyim.biz.api.dto.message.SendMsgResultDto;
import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.c2s.Message;
import com.easyim.biz.api.protocol.c2s.MessageAck;
import com.easyim.biz.api.protocol.enums.c2s.EasyImC2sType;
import com.easyim.biz.api.service.message.IMessageService;
import com.easyim.biz.service.c2s.protocol.IC2SProtocolService;

@Service("messagePService")
public class MessagePServiceImpl implements IC2SProtocolService<Message,MessageAck>{

	
	@Resource
	private IMessageService messageService;
	
	@Resource
	private Mapper mapper;
	
	@Override
	public EasyImC2sType getType() {
		return EasyImC2sType.message;
	}

	@Override
	public MessageAck handleProtocolBody(UserSessionDto userSessionDto,Message message,
			Map<String, String> extendsMap) {
		
		String sessionId = userSessionDto.getSessionId();
		
		SendMsgDto sendMsgDto = mapper.map(message, SendMsgDto.class);
		
		SendMsgResultDto  result = messageService.sendMsg(sendMsgDto,sessionId);
		
		
		MessageAck messageAck = new MessageAck();
		messageAck.setResult(result.getResult());
		
		return messageAck;
	}


}
