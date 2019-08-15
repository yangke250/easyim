package com.easyim.biz.service.conversation.impl;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.easyim.biz.api.service.conversation.IConversationService;
import com.easyim.biz.constant.Constant;
import com.easyim.biz.domain.ConversationDo;
import com.easyim.biz.domain.ProxyConversationDo;
import com.easyim.biz.mapper.conversation.IConversationMapper;

import cn.linkedcare.springboot.redis.template.RedisTemplate;

@Service(interfaceClass=IConversationService.class)
public class ConversationServiceImpl implements IConversationService{

	@Resource
	private IConversationMapper conversationMapper;

	@Resource
	private RedisTemplate redisTemplate;
	
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
	

	/**
	 * 得到未读消息的key
	 * @param tenementId
	 * @param userId
	 * @return
	 */
	public String getUnreadKey(long cid){
		String key = Constant.UNREAD_MSG_KEY  + "_" + cid;
		return key;
	}
		


	@Override
	public void increaseUnread(long cid) {
		String key = getUnreadKey(cid);
		
		this.redisTemplate.incr(key);
	}

	@Override
	public void cleanUnread(long cid) {
		String key = getUnreadKey(cid);
		
		this.redisTemplate.del(key);
	}

}
