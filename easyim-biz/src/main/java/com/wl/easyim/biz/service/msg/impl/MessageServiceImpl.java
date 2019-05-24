package com.wl.easyim.biz.service.msg.impl;

import java.util.Calendar;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.stereotype.Service;

import com.wl.easy.springboot.hbase.api.HbaseTemplate;
import com.wl.easy.springboot.redis.template.RedisTemplate;
import com.wl.easyim.biz.api.dto.message.MessageSendDto;
import com.wl.easyim.biz.api.dto.message.MessageSendResultDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.ResourceType;
import com.wl.easyim.biz.api.protocol.enums.c2s.Result;
import com.wl.easyim.biz.api.service.message.IMessageService;
import com.wl.easyim.biz.constant.Constant;
import com.wl.easyim.biz.domain.ConversationDo;
import com.wl.easyim.biz.domain.MessageDo;
import com.wl.easyim.biz.domain.ProxyConversationDo;
import com.wl.easyim.biz.domain.TenementDo;
import com.wl.easyim.biz.mapper.conversation.IConversationMapper;
import com.wl.easyim.biz.mapper.conversation.IProxyConversationMapper;
import com.wl.easyim.biz.mapper.tenement.ITenementMapper;
import com.wl.easyim.biz.service.conversation.IConversationService;
import com.wl.easyim.biz.service.conversation.IProxyConversationService;

import lombok.extern.slf4j.Slf4j;


@Service("messageService")
@Slf4j
public class MessageServiceImpl implements IMessageService {
	
	public final static long MAX_NUM = 500;
	
	public final static int OFFLINE_TIME = 15*24*60*60;//离线消息，最多15天
	
	@Resource
	private HbaseTemplate baseTemplate;
	
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
	 * 验证相关对象
	 * @param message
	 * @return
	 */
	private boolean doValidator(MessageSendDto message){
		Set<ConstraintViolation<MessageSendDto>> results = validator.validate(message);
		if(results.size()>0){
			for(ConstraintViolation<MessageSendDto> result:results){
				log.error("messageServiceImpl doValidator error:{}",result.getMessage());
			}
			return false;
		}
		return true;
	}
	
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
	 * 保存离线消息
	 * @param tenementId
	 * @param toId
	 * @param msgId
	 * @param isMultiDevice
	 */
	private void saveOfflineMsg(long tenementId,String toId,long msgId,boolean isMultiDevice){
		String key = Constant.OFFLINE_MSG_KEY+tenementId+"_"+toId;
		
		//多设备离线消息
		if(isMultiDevice){
			saveOfflineMsg(key+ResourceType.pc,msgId);
			saveOfflineMsg(key+ResourceType.app,msgId);
		}else{
			saveOfflineMsg(key,msgId);
		}
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
	
	private void pushMsg(long msgId,MessageSendDto message){
		
	}
	
	
	@Override
	public MessageSendResultDto sendMessage(MessageSendDto message) {
        //生产msgId
		long msgId = getId();
        
        MessageSendResultDto dto = new MessageSendResultDto();
        
        TenementDo tenement =  tenementMapper.getTenementById(message.getTenementId());
        
        boolean result = doValidator(message);
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
		saveOfflineMsg(tenementId,toId,msgId,tenement.isMultiDevice());
		
		//保证未读消息数
		saveUnreadCount(cid,toId);
		
		//保存消息
		saveMsg(null);
		
		return null;
	}
	
	
	
	private long getId(){
		return redisTemplate.incr(Constant.ID_KEY);
	}

}
