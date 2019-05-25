package com.wl.easyim.biz.service.conversation.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wl.easyim.biz.mapper.conversation.IConversationMapper;
import com.wl.easyim.biz.service.conversation.IConversationService;

@Service("conversationService")
public class ConversationServiceImpl implements IConversationService{

	@Resource
	private IConversationMapper conversationMapper;
	
	@Override
	public long getCid(long tenementId, String fromId, String toId, long proxyCid) {
		// TODO Auto-generated method stub
		return 0;
	}

}
