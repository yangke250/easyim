package com.easyim.biz.service.msg.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
//import com.easy.springboot.redis.template.RedisTemplate;
import com.easyim.biz.Launch;
import com.easyim.biz.api.dto.message.OfflineMsgDto;
import com.easyim.biz.api.dto.message.SendMsgDto;
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

	public final static long MAX_NUM = 500;

	public final static int MAX_OFFLINE_NUM = 50;

	public final static int OFFLINE_TIME = 15 * 24 * 60 * 60;// 离线消息，最多15天
	
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

	// @Resource
	// private HbaseTemplate baseTemplate;

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
	 */
	private void saveOfflineMsg(String key, long msgId, C2sProtocol c2sProtocol) {

		// 序列化
//		byte[] bytes = null;
//		try {
//			bytes = simpleTypeCodec.encode(c2sProtocol);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
		
		redisTemplate.zadd(key, Double.parseDouble(String.valueOf(msgId)),JSON.toJSONString(c2sProtocol));
		long count = redisTemplate.zcard(key);
		if (count > MAX_NUM) {// 离线消息超过最大数
			redisTemplate.zremrangeByRank(key, 0, Integer.parseInt((count - MAX_NUM) + ""));
		}

		redisTemplate.expire(key, OFFLINE_TIME);
	}

	/**
	 * 离线消息的key
	 * 
	 * @param tenementId
	 * @param toId
	 * @return
	 */
	private String getOfflineKey(long tenementId, String toId) {
		String key = Constant.OFFLINE_MSG_KEY + tenementId + "_" + toId;
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

		String key = getOfflineKey(messagePush.getTenementId(), messagePush.getToId());
		// 多设备离线消息
		saveOfflineMsg(key, messagePush.getId(), c2sProtocol);

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
		
		this.messageMapper.insertMessage(message);
	
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

	private List<C2sProtocol> pullOfflineMsg(String key, long lastMsgId) {


		Set<Tuple> sets = null;
		if (lastMsgId <= 0) {
			//查询最近的
			sets = redisTemplate.zrangeWithScores(key, 0, MAX_OFFLINE_NUM);
		} else {//最小id，为lastMsgId+1
			sets = redisTemplate.zrangeByScoreWithScores(key,Double.parseDouble(String.valueOf(lastMsgId+1)), Double.MAX_VALUE, 0, MAX_OFFLINE_NUM);
		}

		List<C2sProtocol> list = new ArrayList<C2sProtocol>();
		for (Tuple set : sets) {
//			try {
//				byte[] bytes = set.getBinaryElement();
//				System.out.println(bytes.length);
//				C2sProtocol newStt = simpleTypeCodec.decode(bytes);
				list.add(JSON.parseObject(set.getElement(),C2sProtocol.class));
//			} catch (IOException e) {
//				e.printStackTrace();// ignore
//				log.error("exception:{}", e);
//			}

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

		String key = getOfflineKey(tenementId, userId);
		long lastMsgId = offlineMsgDto.getLastMsgId();

		return pullOfflineMsg(key, lastMsgId);
	}

	@Override
	public void sendMsg(SendMsgDto message, List<String> userIds) {
		sendMsg(message,userIds,null);
	}
	
	@Override
	public void sendMsg(SendMsgDto message, List<String> userIds,SendMsgListener sendMsgListener) {

		Flowable<SendMsgDto> upstream = Flowable.create(new FlowableOnSubscribe<SendMsgDto>() {
			@Override
			public void subscribe(FlowableEmitter<SendMsgDto> emitter) throws Exception {
				for(String userId:userIds){
					SendMsgDto messageMapper = mapper.map(message,SendMsgDto.class);
					messageMapper.setToId(userId);
					
					emitter.onNext(messageMapper);
				}
				emitter.onComplete();
			}
		}, BackpressureStrategy.BUFFER); 
		
		Subscriber<SendMsgDto> subscriber = new Subscriber<SendMsgDto>() {
			@Override
			public void onSubscribe(Subscription s) {
				s.request(Long.MAX_VALUE);
			}

			@Override
			public void onNext(SendMsgDto dto) {
				 SendMsgResultDto  sendMsgResultDto = sendMsg(dto);
				 if(sendMsgListener!=null&&sendMsgResultDto.getResult()==Result.success){
					 sendMsgListener.callback(sendMsgResultDto.getMessagePush());
				 }
			}

			@Override
			public void onError(Throwable t) {
			}

			@Override
			public void onComplete() {
			}
		};
		upstream.subscribeOn(Schedulers.io(), true).observeOn(Schedulers.computation()).subscribe(subscriber);
	}
	

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		// TODO Auto-generated method stub
		
	}
}