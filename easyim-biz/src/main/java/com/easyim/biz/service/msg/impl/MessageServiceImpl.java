package com.easyim.biz.service.msg.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;


import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.stereotype.Service;

import com.easy.springboot.redis.template.RedisTemplate;
import com.easyim.biz.Launch;
import com.easyim.biz.api.dto.message.OfflineMsgDto;
import com.easyim.biz.api.dto.message.SendMsgDto;
import com.easyim.biz.api.dto.message.SendMsgResultDto;
import com.easyim.biz.api.protocol.enums.c2s.ResourceType;
import com.easyim.biz.api.protocol.enums.c2s.Result;
import com.easyim.biz.api.protocol.protocol.c2s.MessagePush;
import com.easyim.biz.api.service.conversation.IConversationService;
import com.easyim.biz.api.service.conversation.IProxyConversationService;
import com.easyim.biz.api.service.message.IMessageService;
import com.easyim.biz.constant.Constant;
import com.easyim.biz.domain.ConversationDo;
import com.easyim.biz.domain.MessageDo;
import com.easyim.biz.domain.ProxyConversationDo;
import com.easyim.biz.domain.TenementDo;
import com.easyim.biz.mapper.conversation.IConversationMapper;
import com.easyim.biz.mapper.conversation.IProxyConversationMapper;
import com.easyim.biz.mapper.tenement.ITenementMapper;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Tuple;


@Service
@Slf4j
public class MessageServiceImpl implements IMessageService {
	
	public final static long MAX_NUM = 500;
	
	public final static int MAX_OFFLINE_NUM =20;
	
	public final static int OFFLINE_TIME = 15*24*60*60;//离线消息，最多15天
	
//	@Resource
//	private HbaseTemplate baseTemplate;
	
	@Resource
	private RedisTemplate redisTemplate;
	
	@Resource
	private ITenementMapper tenementMapper;
	
	@Resource
	private IConversationService conversationService;
	
	@Resource
	private IProxyConversationService proxyConversationService;
	

	
	@Resource
	private Validator validator;
	
	
	
	
	/**
	 * 保存离线消息
	 * @param key
	 * @param msgId
	 */
	private void saveOfflineMsg(String key,long msgId){
		redisTemplate.zadd(key,msgId,String.valueOf(msgId));
		long count = redisTemplate.zcard(key);
		if(count>MAX_NUM){//离线消息超过最大数
			redisTemplate.zremrangeByRank(key,0,Integer.parseInt((count-MAX_NUM)+""));
		}
		redisTemplate.zadd(key,msgId,String.valueOf(msgId));
		
		redisTemplate.expire(key,OFFLINE_TIME);
	}
	
	/**
	 * 离线消息的key
	 * @param tenementId
	 * @param toId
	 * @return
	 */
	private String getOfflineKey(long tenementId,String toId){
		String key = Constant.OFFLINE_MSG_KEY+tenementId+"_"+toId;
		return key;
	}
	/**
	 * 保存离线消息
	 * @param tenementId
	 * @param toId
	 * @param msgId
	 * @param isMultiDevice
	 */
	private void saveOfflineMsg(long tenementId,String toId,long msgId){
		String key = getOfflineKey(tenementId,toId);
		
		//多设备离线消息
		saveOfflineMsg(key,msgId);
	}

	/**
	 * 保存未读消息数
	 * @param cid
	 * @param toId
	 */
	private void saveUnreadCount(long cid,String toId){
		String key = Constant.UNREAD_CID_KEY+"_"+cid+"_"+toId;
		redisTemplate.incr(key);
	}
	
	
	/**
	 * 保存未读消息数
	 * @param cid
	 * @param toId
	 */
	private void saveMsg(MessageDo message){
		
	}
	

	
	@Override
	public SendMsgResultDto sendMsg(SendMsgDto message) {
        //生产msgId
		long msgId = getId();
        
        SendMsgResultDto dto = new SendMsgResultDto();
        
        TenementDo tenement =  tenementMapper.getTenementById(message.getTenementId());
        
        boolean result = Launch.doValidator(message);
        if(tenement==null||!result){
        	dto.setResult(Result.inputError);
        	return dto;
        }
        
        //得到代理会话
        long tenementId  = message.getTenementId();
        long proxyCid    = message.getProxyCid();
        String proxyFromId = message.getProxyFromId();
        String proxyToId   = message.getProxyToId();
        if(proxyCid==0){
			proxyCid = proxyConversationService.getProxyCid(tenementId, proxyFromId, proxyToId);
		}
		
		long cid =  message.getCid();
		String fromId = message.getFromId();
        String toId   = message.getToId();
        if(cid==0){
			cid = conversationService.getCid(tenementId,fromId,toId,proxyCid);
		}
		
        //保存离线消息
		saveOfflineMsg(tenementId,toId,msgId);
		
		//保证未读消息数
		saveUnreadCount(cid,toId);
		
		//保存消息
		saveMsg(null);
		
		return null;
	}
	
	
	
	private long getId(){
		return redisTemplate.incr(Constant.ID_KEY);
	}

	private List<MessagePush> pullOfflineMsg(String key,long lastMsgId){
		
		List<MessagePush> message = new ArrayList<MessagePush>();
		
		Set<Tuple> sets = null;
		if(lastMsgId<=0){
			sets = redisTemplate.zrangeWithScores(key,0,MAX_OFFLINE_NUM);
		}else{
			sets = redisTemplate.zrangeByScoreWithScores(key,lastMsgId,Double.MAX_VALUE,0,MAX_OFFLINE_NUM);
		}
		
		List<Long> ids = new ArrayList<Long>();
		for(Tuple set:sets){
			String str = new String(set.getBinaryElement());
			
			ids.add(Long.getLong(str));
		}
		
		return message;
	}

	

	@Override
	public List<MessagePush> pullOfflineMsg(OfflineMsgDto offlineMsgDto){
		
		List<MessagePush> list = new ArrayList<MessagePush>();
		
		boolean result = Launch.doValidator(offlineMsgDto);
		if(!result){
			return list;
		}
		
		long tenementId = offlineMsgDto.getTenementId();
		String userId   = offlineMsgDto.getUserId();
		TenementDo  tenement = this.tenementMapper.getTenementById(offlineMsgDto.getTenementId());
		if(tenement==null){
			return list;
		}
		
		String key = getOfflineKey(tenementId,userId);
		long lastMsgId = offlineMsgDto.getLastMsgId();
		
		return pullOfflineMsg(key,lastMsgId);
	}

}
