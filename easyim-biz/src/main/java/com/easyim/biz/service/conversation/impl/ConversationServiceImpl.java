package com.easyim.biz.service.conversation.impl;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.easyim.biz.api.service.conversation.IConversationService;
import com.easyim.biz.mapper.conversation.IConversationMapper;

@Service(interfaceClass=IConversationService.class)
public class ConversationServiceImpl implements IConversationService{

	@Resource
	private IConversationMapper conversationMapper;
	
	@Override
	public long getCid(long tenementId, String fromId, String toId, long proxyCid) {
		// TODO Auto-generated method stub
		return 0;
	}

}
