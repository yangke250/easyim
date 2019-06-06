package com.easyim.biz.service.conversation.impl;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.easyim.biz.api.service.conversation.IConversationService;
import com.easyim.biz.domain.ConversationDo;
import com.easyim.biz.domain.ProxyConversationDo;
import com.easyim.biz.mapper.conversation.IConversationMapper;

@Service(interfaceClass=IConversationService.class)
public class ConversationServiceImpl implements IConversationService{

	@Resource
	private IConversationMapper conversationMapper;
	
	@Override
	public long getCid(long tenementId, String fromId, String toId, long proxyCid) {
		String smallId;
		String bigId;
		if(fromId.compareTo(toId)>0){
			bigId   = fromId;
			smallId = toId;
		}else{
			bigId   = toId;
			smallId = fromId;
		}
		
		ConversationDo conversation = conversationMapper.getConversation(tenementId,smallId,bigId);
		if(conversation==null){
			conversation = new ConversationDo();
			conversation.setTenementId(tenementId);
			conversation.setProxyCid(proxyCid);
			conversation.setSmallId(smallId);
			conversation.setBigId(bigId);
			conversationMapper.insertConversationDo(conversation);
		}

		return conversation.getId();
	}

}
