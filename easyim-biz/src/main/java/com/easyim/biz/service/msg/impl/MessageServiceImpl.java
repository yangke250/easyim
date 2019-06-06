package com.easyim.biz.service.msg.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;


import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
//import com.easy.springboot.redis.template.RedisTemplate;
import com.easyim.biz.Launch;
import com.easyim.biz.api.dto.message.OfflineMsgDto;
import com.easyim.biz.api.dto.message.SendMsgDto;
import com.easyim.biz.api.dto.message.SendMsgResultDto;
import com.easyim.biz.api.dto.protocol.C2sProtocol;
import com.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.easyim.biz.api.protocol.enums.c2s.ResourceType;
import com.easyim.biz.api.protocol.enums.c2s.Result;
import com.easyim.biz.api.protocol.c2s.MessagePush;
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
import com.easyim.biz.mapper.message.IMessageMapper;
import com.easyim.biz.mapper.tenement.ITenementMapper;
import com.easyim.route.service.IProtocolRouteService;

import cn.linkedcare.springboot.redis.template.RedisTemplate;
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
	private IMessageMapper messageMapper;
	
	@Resource
	private IProxyConversationService proxyConversationService;
	
	@Resource
	private IProtocolRouteService protocolRouteService;
	
	@Resource
	private Mapper mapper;
	
	
	/**
	 * 保存离线消息
	 * @param key
	 * @param msgId
	 */
	private void saveOfflineMsg(String key,C2sProtocol c2sProtocol){
		
		MessagePush messagePush =(MessagePush)c2sProtocol.getBody();
		
		Codec<C2sProtocol> simpleTypeCodec = ProtobufProxy
                .create(C2sProtocol.class);

        // 序列化
        byte[] bytes = null;
		try {
			bytes = simpleTypeCodec.encode(c2sProtocol);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		redisTemplate.zadd(key,Double.parseDouble(String.valueOf(messagePush.getId())),bytes);
		long count = redisTemplate.zcard(key);
		if(count>MAX_NUM){//离线消息超过最大数
			redisTemplate.zremrangeByRank(key,0,Integer.parseInt((count-MAX_NUM)+""));
		}
		
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
	private C2sProtocol saveOfflineMsg(MessagePush messagePush){
		C2sProtocol c2sProtocol = new C2sProtocol();
		
		c2sProtocol.setType(C2sCommandType.messagePush);
		c2sProtocol.setBody(messagePush);
		
		String key = getOfflineKey(messagePush.getTenementId(),messagePush.getToId());
		//多设备离线消息
		saveOfflineMsg(key,c2sProtocol);
		
		return c2sProtocol;
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
	
	public static void main(String[] args) throws IOException{
		MessagePush msgPush = new MessagePush();
		msgPush.setId(50000);
		msgPush.setContent("asdasdsads");
		
		Codec<MessagePush> simpleTypeCodec = ProtobufProxy
                .create(MessagePush.class);

        
		
        // 序列化
        byte[] bb = simpleTypeCodec.encode(msgPush);
        
        System.out.println(bb.length);

        long now = System.currentTimeMillis();
        // 反序列化
        MessagePush newStt = simpleTypeCodec.decode(bb);
        newStt = simpleTypeCodec.decode(bb);
        newStt = simpleTypeCodec.decode(bb);
        newStt = simpleTypeCodec.decode(bb);
        newStt = simpleTypeCodec.decode(bb);
        newStt = simpleTypeCodec.decode(bb);
        newStt = simpleTypeCodec.decode(bb);
        newStt = simpleTypeCodec.decode(bb);
        newStt = simpleTypeCodec.decode(bb);
        newStt = simpleTypeCodec.decode(bb);
        newStt = simpleTypeCodec.decode(bb);
        System.out.println((System.currentTimeMillis()-now));
	}
	
	private MessagePush buildMessagePush(){
		MessagePush msgPush = new MessagePush();
//		msgPush.setId(id);
//		msgPush.setContent(content);
//		
		return msgPush;
	}
	
	/**
	 * 保存未读消息数
	 * @param cid
	 * @param toId
	 */
	private void saveMsg(MessagePush messagePush,String proxyFromId,String proxyToId){
		MessageDo message =mapper.map(messagePush,MessageDo.class);
		message.setProxyFromId(proxyFromId);
		message.setProxyToId(proxyToId);
		
		this.messageMapper.insertMessage(message);
	}
	

	
	@Override
	public SendMsgResultDto sendMsg(SendMsgDto messageDto) {
        //生产msgId
		long msgId = getId();
        
        SendMsgResultDto dto = new SendMsgResultDto();
        
        TenementDo tenement =  tenementMapper.getTenementById(messageDto.getTenementId());
        
        boolean result = Launch.doValidator(messageDto);
        if(tenement==null||!result){
        	dto.setResult(Result.inputError);
        	return dto;
        }
        
        //得到代理会话
        long tenementId  = messageDto.getTenementId();
        long proxyCid    = messageDto.getProxyCid();
        String fromId = messageDto.getFromId();
        String toId   = messageDto.getToId();
    	String proxyFromId = messageDto.getProxyFromId();
    	String proxyToId   = messageDto.getProxyToId();

        if(proxyCid==0){
            if(StringUtils.isEmpty(proxyFromId)){
            	proxyFromId = fromId;
            }
        	if(StringUtils.isEmpty(proxyToId)){
            	proxyToId = toId;
            }
			proxyCid = proxyConversationService.getProxyCid(tenementId, proxyFromId, proxyToId);
		}
		
		long cid =  messageDto.getCid();
		if(cid==0){
			cid = conversationService.getCid(tenementId,fromId,toId,proxyCid);
		}
		
        //build msg push
        MessagePush messagePush = new MessagePush();
        messagePush.setId(msgId);
        messagePush.setBizUuid(messageDto.getBizUid());
        messagePush.setCid(cid);
        messagePush.setContent(messageDto.getContent());
        messagePush.setFromId(fromId);
        messagePush.setId(msgId);
        messagePush.setProxyCid(proxyCid);
        messagePush.setSubType(messageDto.getSubType());
        messagePush.setTenementId(tenementId);
        messagePush.setToId(toId);
        messagePush.setType(messageDto.getType().getValue());
        
        //保存消息
      	saveMsg(messagePush,proxyFromId,proxyToId);
        
        //保存离线消息
      	C2sProtocol c2sProtocol = saveOfflineMsg(messagePush);		
		
		this.protocolRouteService.route(tenementId,toId,JSON.toJSONString(c2sProtocol));
		
		return new SendMsgResultDto();
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
