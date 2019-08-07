package com.easyim.biz.service.msg.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
//import com.easy.springboot.redis.template.RedisTemplate;
import com.easyim.biz.Launch;
import com.easyim.biz.api.dto.message.OfflineMsgDto;
import com.easyim.biz.api.dto.message.SendMsgDto;
import com.easyim.biz.api.dto.message.SendMsgDto.MessageType;
import com.easyim.biz.api.dto.message.SendMsgResultDto;
import com.easyim.biz.api.dto.protocol.C2sProtocol;
import com.easyim.biz.api.listener.SendMsgListener;
import com.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.easyim.biz.api.protocol.enums.c2s.EnventType;
import com.easyim.biz.api.protocol.enums.c2s.ResourceType;
import com.easyim.biz.api.protocol.enums.c2s.Result;
import com.easyim.biz.api.protocol.c2s.MessagePush;
import com.easyim.biz.api.service.conversation.IConversationService;
import com.easyim.biz.api.service.conversation.IProxyConversationService;
import com.easyim.biz.api.service.message.IMessageSearchService;
import com.easyim.biz.api.service.message.IMessageService;
import com.easyim.biz.constant.Constant;
import com.easyim.biz.domain.ConversationDo;
import com.easyim.biz.domain.MessageDo;
import com.easyim.biz.domain.ProxyConversationDo;
import com.easyim.biz.domain.TenementDo;
import com.easyim.biz.listener.EnventListenerMap;
import com.easyim.biz.mapper.conversation.IConversationMapper;
import com.easyim.biz.mapper.conversation.IProxyConversationMapper;
import com.easyim.biz.mapper.message.IMessageMapper;
import com.easyim.biz.mapper.tenement.ITenementMapper;
import com.easyim.biz.task.SynMessageTask;
import com.easyim.route.service.IProtocolRouteService;

import cn.linkedcare.springboot.redis.template.RedisTemplate;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Tuple;
import redis.clients.util.SafeEncoder;

@Slf4j
@Service(interfaceClass = IMessageService.class)
public class MessageServiceImpl implements IMessageService,BeanFactoryAware {

	public final static long MAX_OFFLINE_NUM = 50;

	@Value("${offline.msg.nums}")
	private int MAX_GET_OFFLINE_NUM = 50;

	public final static int OFFLINE_TIME = 15 * 24 * 60 * 60;// 离线消息，最多15天
	
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

	public final static String CHARSET = "UTF-8";
	
	
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

	@Resource
	private BeanFactory beanFactory;
	
	private Codec<C2sProtocol> simpleTypeCodec = ProtobufProxy.create(C2sProtocol.class);

	/**
	 * 保存离线消息
	 * 
	 * @param key
	 * @param msgId
	 * @throws IOException 
	 */
	private void saveOfflineMsg(String key, long msgId, C2sProtocol c2sProtocol) throws IOException {

		// 序列化
		byte[] msg = simpleTypeCodec.encode(c2sProtocol);
		
		String offlineMsgKey = getOfflineMsgKey(msgId);
		//保存离线消息id list
		redisTemplate.zadd(key, Double.parseDouble(String.valueOf(msgId)),JSON.toJSONString(msgId));
		
		redisTemplate.setex(offlineMsgKey.getBytes(CHARSET),OFFLINE_TIME,msg);
		
		long count = redisTemplate.zcard(key);
		if (count > MAX_OFFLINE_NUM) {// 离线消息超过最大数
			int end = Integer.parseInt((count - MAX_OFFLINE_NUM) + "");
			
			Set<String> ids = redisTemplate.zrange(key, 0, end);
			for(String id:ids){
				String outSizeMsgKey = getOfflineMsgKey(Long.parseLong(id));
				redisTemplate.del(outSizeMsgKey.getBytes(CHARSET));
			}
			
			redisTemplate.zremrangeByRank(key, 0, Integer.parseInt((count - MAX_OFFLINE_NUM) + ""));
		}

	}

	/**
	 * 离线消息的的key
	 * 
	 * @param tenementId
	 * @param toId
	 * @return
	 */
	private String getOfflineMsgKey(long msgId) {
		String key = Constant.OFFLINE_MSG_KEY +msgId;
		return key;
	}
	
	/**
	 * 离线消息的id列表的key
	 * 
	 * @param tenementId
	 * @param toId
	 * @return
	 */
	private String getOfflineSetKey(long tenementId, String toId) {
		String key = Constant.OFFLINE_MSG_SET_KEY + tenementId + "_" + toId;
		return key;
	}

	/**
	 * 保存离线消息
	 * 
	 * @param tenementId
	 * @param toId
	 * @param msgId
	 * @param isMultiDevice
	 */
	private C2sProtocol saveOfflineMsg(MessagePush messagePush) {
		C2sProtocol c2sProtocol = new C2sProtocol();

		c2sProtocol.setType(C2sCommandType.messagePush);
		c2sProtocol.setBody(JSON.toJSONString(messagePush));

		if(MessageType.isSaveOffline(messagePush.getType())){
			String key = getOfflineSetKey(messagePush.getTenementId(), messagePush.getToId());
			// 多设备离线消息
			try {
				saveOfflineMsg(key, messagePush.getId(), c2sProtocol);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		return c2sProtocol;
	}


	/**
	 * 保存未读消息数
	 * 
	 * @param cid
	 * @param toId
	 */
	private MessageDo saveMsg(MessagePush messagePush, String proxyFromId, String proxyToId) {
		MessageDo message = mapper.map(messagePush, MessageDo.class);
		message.setProxyFromId(proxyFromId);
		message.setProxyToId(proxyToId);
		message.setGmtCreate(new Date());
		
		if(MessageType.isSaveDb(messagePush.getType())){
			this.messageMapper.insertMessage(message);
		}
	
		return message;
	}

	@Override
	public SendMsgResultDto sendMsg(SendMsgDto messageDto) {
		// 生产msgId
		long msgId = getId();
		log.info("sendMsg msg:{},{}",msgId,messageDto.getToId());

		SendMsgResultDto dto = new SendMsgResultDto();

		TenementDo tenement = tenementMapper.getTenementById(messageDto.getTenementId());

		boolean result = Launch.doValidator(messageDto);
		if (tenement == null || !result) {
			log.warn("sendMsg error:{},{}",messageDto.getToId(),JSON.toJSONString(messageDto));
			dto.setResult(Result.inputError);
			return dto;
		}

		// 得到代理会话
		long tenementId = messageDto.getTenementId();
		long proxyCid = messageDto.getProxyCid();
		String fromId = messageDto.getFromId();
		String toId = messageDto.getToId();
		String proxyFromId = messageDto.getProxyFromId();
		String proxyToId = messageDto.getProxyToId();

		if (proxyCid == 0) {
			if (StringUtils.isEmpty(proxyFromId)) {
				proxyFromId = fromId;
			}
			if (StringUtils.isEmpty(proxyToId)) {
				proxyToId = toId;
			}
			proxyCid = proxyConversationService.getProxyCid(tenementId, proxyFromId, proxyToId);
		}

		long cid = messageDto.getCid();
		if (cid == 0) {
			cid = conversationService.getCid(tenementId, fromId, toId, proxyCid);
		}
		
		log.info("sendMsg msg:{},{} cid succ",msgId,messageDto.getToId());

		
		// build msg push
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
		
		// 保存db消息
		MessageDo messageDo = saveMsg(messagePush, proxyFromId, proxyToId);
		messagePush.setTime(sdf.format(messageDo.getGmtCreate()));
		
		log.info("messagePush:{},{}",msgId,messageDto.getToId());
		//保存离线消息
		C2sProtocol c2sProtocol = saveOfflineMsg(messagePush);
		
		log.info("sendMsg msg:{},{} offline succ",msgId,messageDto.getToId());

		//路由协议
		this.protocolRouteService.route(tenementId, toId, JSON.toJSONString(c2sProtocol));

		log.info("sendMsg msg:{},{} route succ",msgId,messageDto.getToId());

		SendMsgResultDto sendMsgResultDto = new SendMsgResultDto();
		sendMsgResultDto.setMessagePush(messagePush);
		
		EnventListenerMap.callBack(EnventType.sendMsg,messagePush);
		return sendMsgResultDto;
	}

	private long getId() {
		return redisTemplate.incr(Constant.ID_KEY);
	}

	private List<C2sProtocol> pullOfflineMsg(String key, long lastMsgId)  {


		Set<Tuple> sets = null;
		if (lastMsgId <= 0) {
			//查询最近的
			sets = redisTemplate.zrangeWithScores(key, 0, MAX_GET_OFFLINE_NUM);
		} else {//最小id，为lastMsgId+1
			sets = redisTemplate.zrangeByScoreWithScores(key,Double.parseDouble(String.valueOf(lastMsgId+1)), Double.MAX_VALUE, 0, MAX_GET_OFFLINE_NUM);
		}

		
		List<byte[]> idsKey = new ArrayList<byte[]>();
		for (Tuple set : sets) {
				String offlineKey = this.getOfflineMsgKey(Long.parseLong(set.getElement()));
				try {
					idsKey.add(offlineKey.getBytes(CHARSET));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
		}

		byte[][] bytes = idsKey.toArray(new byte[][]{});
		List<byte[]> c2sBytes = this.redisTemplate.mget(bytes);
		
		
		List<C2sProtocol> list = new ArrayList<C2sProtocol>();
		for(byte[] b:c2sBytes){
			C2sProtocol newStt = null;
			try {
				newStt = simpleTypeCodec.decode(b);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
			list.add(newStt);
		}
		
		return list;
	}

	@Override
	public List<C2sProtocol> pullOfflineMsg(OfflineMsgDto offlineMsgDto) {

		List<C2sProtocol> list = new ArrayList<C2sProtocol>();

		boolean result = Launch.doValidator(offlineMsgDto);
		if (!result) {
			return list;
		}

		long tenementId = offlineMsgDto.getTenementId();
		String userId = offlineMsgDto.getUserId();
		TenementDo tenement = this.tenementMapper.getTenementById(offlineMsgDto.getTenementId());
		if (tenement == null) {
			return list;
		}

		String key = getOfflineSetKey(tenementId, userId);
		long lastMsgId = offlineMsgDto.getLastMsgId();

		return pullOfflineMsg(key, lastMsgId);
	}

	@Override
	public void sendMsg(SendMsgDto message, List<String> userIds) {
		sendMsg(message,userIds,null);
	}
	
	@Override
	public void sendMsg(SendMsgDto message, List<String> userIds,SendMsgListener sendMsgListener) {
			for(String userId:userIds){
					SendMsgDto dto = mapper.map(message,SendMsgDto.class);
					dto.setToId(userId);
					
					SynMessageTask.addTask(dto);
			}
	}
	

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		// TODO Auto-generated method stub
		
	}
}