package com.easyim.biz.service.test;

import javax.annotation.Resource;

import org.junit.Test;

import com.easyim.biz.api.dto.message.SendMsgDto;
import com.easyim.biz.api.dto.message.SendMsgResultDto;
import com.easyim.biz.api.dto.message.SendMsgDto.MessageType;
import com.easyim.biz.api.service.message.IMessageService;
import com.easyim.biz.test.LaunchTest;

public class MessageImplTest extends LaunchTest{
	@Resource
	private IMessageService msgService;
	
	@Test
	public void insertMsg(){
		SendMsgDto message = new SendMsgDto();
		message.setContent("aaaaa");
		message.setFromId("333");
		message.setSubType("11122");
		message.setTenementId(1);
		message.setToId("1111");
		message.setType(MessageType.text);
		SendMsgResultDto  result = msgService.sendMsg(message);
		System.out.println("result:"+result);
	}
}
