package com.easyim.biz.service.msg.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.easyim.biz.Launch;
import com.easyim.biz.api.dto.message.SendMsgDto;
import com.easyim.biz.api.service.message.IMessageService;
import com.easyim.biz.api.service.message.IMessageTransferService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(interfaceClass = IMessageTransferService.class)
public class MessageTransferServiceImpl implements IMessageTransferService{

	@Override
	public void transferMessage(long id, SendMsgDto messageDto){

		boolean result = Launch.doValidator(messageDto);
		
		if(!result){
			log.error("transferMessage error:{},{}",id,JSON.toJSONString(messageDto));
			return;
		}

	}

}
