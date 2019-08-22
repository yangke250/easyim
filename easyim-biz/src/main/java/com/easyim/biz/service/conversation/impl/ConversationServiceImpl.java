package com.easyim.biz.service.conversation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.dozer.Mapper;

import com.alibaba.dubbo.config.annotation.Service;
import com.easyim.biz.api.dto.conversation.ConversationDto;
import com.easyim.biz.api.dto.message.SendMsgDto.MessageType;
import com.easyim.biz.api.protocol.c2s.MessagePush;
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
	
	@Resource
	private Mapper mapper;
	
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
	public void increaseUnread(int msgType,long cid) {
		if(!MessageType.isIncrementUnread(msgType)){
			return;
		}
		
		String key = getUnreadKey(cid);
		
		this.redisTemplate.incr(key);
	}

	@Override
	public void cleanUnread(long cid) {
		String key = getUnreadKey(cid);
		
		this.redisTemplate.del(key);
	}

	/**
	 * 最近消息列表的key
	 * 
	 * @param tenementId
	 * @param toId
	 * @return
	 */
	private String getRecentlyKey(long tenementId, String userId) {
		String key = Constant.RECENTLY_KEY + tenementId + "_" + userId;
		return key;
	}
	

	@Override
	public void addRecentlyConversation(MessagePush messagePush) {

		long tenementId =  messagePush.getTenementId();
		String toId     =  messagePush.getFromId();
		String fromId   =  messagePush.getToId();
		
		String fromRecentlyCids = getRecentlyKey(tenementId,fromId);
		String toRecentlyCids = getRecentlyKey(tenementId,toId);


		double score=(double)System.currentTimeMillis();
		
		redisTemplate.zadd(fromRecentlyCids,score,String.valueOf(messagePush.getCid()));
		redisTemplate.zremrangeByRank(fromRecentlyCids, 100,Integer.MAX_VALUE);
		
		redisTemplate.zadd(toRecentlyCids,score,String.valueOf(messagePush.getCid()));
	}


	@Override
	public List<ConversationDto> selectRecentlyConversation(long tenementId, String userId) {
		String key = getRecentlyKey(tenementId,userId);
		Set<String> set = this.redisTemplate.zrevrange(key,0,Constant.MAX_RECENTLY_NUM);
		List<Long> ids = new ArrayList<Long>();
		
		for(String s:set){
			ids.add(Long.parseLong(s));
		}
		
		List<ConversationDto> dtos = new ArrayList<ConversationDto>();
		
		List<ConversationDo> cs = this.conversationMapper.selectConversationByIds(tenementId, ids);
		for(ConversationDo c:cs){
			ConversationDto dto = mapper.map(c, ConversationDto.class);
			dto.setCid(c.getId());
			
			dtos.add(dto);
		}
		
		return dtos;
	}

}
