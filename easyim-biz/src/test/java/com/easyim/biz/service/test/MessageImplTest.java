package com.easyim.biz.service.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.easyim.biz.api.dto.message.OfflineMsgDto;
import com.easyim.biz.api.dto.message.SendMsgDto;
import com.easyim.biz.api.dto.message.SendMsgResultDto;
import com.easyim.biz.api.dto.message.SendMsgDto.MessageType;
import com.easyim.biz.api.dto.protocol.C2sProtocol;
import com.easyim.biz.api.service.message.IMessageService;
import com.easyim.biz.test.LaunchTest;

public class MessageImplTest extends LaunchTest{
	@Resource
	private IMessageService msgService;
	
	@Test
	public void sendMsgList() throws InterruptedException{
		SendMsgDto message = new SendMsgDto();
		message.setContent("aaaaa");
		message.setFromId("555");
		message.setToId("666");
		message.setSubType("11122");
		message.setTenementId(1001);
		message.setType(MessageType.text);
		
		List<String> list = new ArrayList<String>();
		list.add("29aafb35-25ec-418d-8309-fa232f0d7eb0_1176");
		
		SendMsgResultDto  result = msgService.sendMsg(message,list);
		Thread.sleep(5000000);
		System.out.println("result:"+result);
	}
	
	
//	@Test
//	public void sendMsg(){
//		SendMsgDto message = new SendMsgDto();
//		message.setContent("aaaaa");
//		message.setFromId("555");
//		message.setToId("29aafb35-25ec-418d-8309-fa232f0d7eb0_1176");
//		message.setSubType("11122");
//		message.setTenementId(1001);
//		message.setType(MessageType.text);
//		SendMsgResultDto  result = msgService.sendMsg(message);
//		System.out.println("result:"+result);
//	}
	
	@Test
	public void pullOfflineMsg(){
		OfflineMsgDto olDto= new OfflineMsgDto();
		olDto.setLastMsgId(0);
		olDto.setTenementId(1001);
		olDto.setUserId("29aafb35-25ec-418d-8309-fa232f0d7eb0_1176");
		
		List<C2sProtocol> list = msgService.pullOfflineMsg(olDto);
		
		System.out.println("result:"+JSON.toJSONString(list));
	}
}
