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
import com.easyim.biz.api.dto.message.ForwardMsgDto;
import com.easyim.biz.api.dto.message.OfflineMsgDto;
import com.easyim.biz.api.dto.message.PullOfflineMsgResultDto;
import com.easyim.biz.api.dto.message.SendMsgDto;
import com.easyim.biz.api.dto.message.SendMsgDto.MessageType;
import com.easyim.biz.api.dto.message.SendMsgResultDto;
import com.easyim.biz.api.dto.protocol.C2sProtocol;
import com.easyim.biz.api.protocol.enums.c2s.EasyImC2sType;
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
import com.easyim.biz.dto.PullOfflineMsgDto;
import com.easyim.biz.listeners.ProtocolListenerFactory;
import com.easyim.biz.mapper.conversation.IConversationMapper;
import com.easyim.biz.mapper.conversation.IProxyConversationMapper;
import com.easyim.biz.mapper.message.IMessageMapper;
import com.easyim.biz.mapper.tenement.ITenementMapper;
import com.easyim.biz.task.SynMessageTask;
import com.easyim.biz.task.SynMessageTask.SynMessageTaskDto;
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


/**
 * 消息相关业务接口
 * @author wl
 *
 */
@Slf4j
@Service(interfaceClass = IMessageService.class)
public class MessageServiceImpl implements IMessageService {
	@Value("${offline.msg.nums}")
	private long MAX_OFFLINE_NUM = 50;

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
		// 保存离线消息id list
		redisTemplate.zadd(key, Double.parseDouble(String.valueOf(msgId)), JSON.toJSONString(msgId));

		redisTemplate.setex(offlineMsgKey.getBytes(Constant.CHARSET), Constant.OFFLINE_TIME, msg);

		long count = redisTemplate.zcard(key);
		if (count > MAX_OFFLINE_NUM) {// 离线消息超过最大数
			int end = Integer.parseInt((count - MAX_OFFLINE_NUM) + "");

			Set<String> ids = redisTemplate.zrange(key, 0, end);
			for (String id : ids) {
				String outSizeMsgKey = getOfflineMsgKey(Long.parseLong(id));
				redisTemplate.del(outSizeMsgKey.getBytes(Constant.CHARSET));
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
		String key = Constant.OFFLINE_MSG_KEY + msgId;
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
	private C2sProtocol saveOfflineMsg(MessagePush messagePush, String toId) {
		C2sProtocol c2sProtocol = new C2sProtocol();

		c2sProtocol.setType(EasyImC2sType.messagePush.getValue());
		c2sProtocol.setBody(JSON.toJSONString(messagePush));

		if (MessageType.isSaveOffline(messagePush.getType())) {
			String key = getOfflineSetKey(messagePush.getTenementId(), toId);
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
	 * 得到未读消息的key
	 * 
	 * @param tenementId
	 * @param userId
	 * @return
	 */
	public String getUnreadKey(long tenementId, long cid) {
		String key = Constant.UNREAD_MSG_KEY + tenementId + "_" + cid;
		return key;
	}

	/**
	 * 保存消息
	 * 
	 * @param cid
	 * @param toId
	 */
	private MessageDo saveMsg(MessagePush messagePush, String proxyFromId, String proxyToId) {
		MessageDo message = mapper.map(messagePush, MessageDo.class);
		message.setProxyFromId(proxyFromId);
		message.setProxyToId(proxyToId);
		message.setGmtCreate(new Date());

		if (MessageType.isSaveDb(messagePush.getType())) {
			this.messageMapper.insertMessage(message);
		}

		return message;
	}

	@Override
	public SendMsgResultDto sendMsg(SendMsgDto messageDto,String excludeSessionId) {
		// 生产msgId
		long msgId = getId();
		log.info("sendMsg msg:{},{}", msgId, messageDto.getToId());

		SendMsgResultDto dto = new SendMsgResultDto();

		TenementDo tenement = tenementMapper.getTenementById(messageDto.getTenementId());

		boolean result = Launch.doValidator(messageDto);
		if (tenement == null || !result) {
			log.warn("sendMsg doValidator error:{},{}", messageDto.getToId(), JSON.toJSONString(messageDto));
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

		log.info("sendMsg msg:{},{} cid succ", msgId, messageDto.getToId());

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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		messagePush.setTime(sdf.format(messageDo.getGmtCreate()));

		log.info("messagePush:{},{}", msgId, messageDto.getToId());
		// 保存离线消息
		C2sProtocol c2sProtocol = saveOfflineMsg(messagePush, messagePush.getToId());

		log.info("sendMsg msg:{},{} offline succ", msgId, messageDto.getToId());

		// 增加会话未读消息数
		this.conversationService.increaseUnread(messagePush.getType(), messagePush.getToId(), cid);
		// 增加最近聊天的会话
		this.conversationService.addRecentlyConversation(messagePush);

		// 路由协议
		this.protocolRouteService.route(tenementId, toId, JSON.toJSONString(c2sProtocol),excludeSessionId);

		log.info("sendMsg msg:{},{} route succ", msgId, messageDto.getToId());

		return new SendMsgResultDto();
	}

	private long getId() {
		return redisTemplate.incr(Constant.ID_KEY);
	}

	private byte[][] getOfflineMsgKeys(String key, long lastMsgId) {
		Set<Tuple> sets = null;
		if (lastMsgId <= 0) {
			// 查询最近的
			sets = redisTemplate.zrangeWithScores(key, 0, Constant.MAX_GET_OFFLINE_NUM);
		} else {// 最小id，为lastMsgId+1
			sets = redisTemplate.zrangeByScoreWithScores(key, Double.parseDouble(String.valueOf(lastMsgId + 1)),
					Double.MAX_VALUE, 0, Constant.MAX_GET_OFFLINE_NUM);
		}

		if (sets.size() <= 0) {
			return null;
		}

		byte[][] bytes = new byte[sets.size()][];
		int i = 0;
		for (Tuple set : sets) {
			String offlineKey = this.getOfflineMsgKey(Long.parseLong(set.getElement()));
			try {
				bytes[i] = (offlineKey.getBytes(Constant.CHARSET));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			i++;
		}

		return bytes;
	}

	private PullOfflineMsgResultDto pullOfflineMsg(String key, long lastMsgId) {

		PullOfflineMsgResultDto dto = new PullOfflineMsgResultDto();
		byte[][] offlineKeys = getOfflineMsgKeys(key, lastMsgId);
		if (offlineKeys == null) {
			return dto;
		}
		
		//还需要拉取更多的消息，防止消息过期，还有部分消息没有同步
		dto.setMore(true);
		
		List<byte[]> c2sBytes = this.redisTemplate.mget(offlineKeys);
		if (c2sBytes == null) {
			return dto;
		}

		List<C2sProtocol> list = new ArrayList<C2sProtocol>();

		for (byte[] b : c2sBytes) {
			C2sProtocol newStt = null;
			try {
				newStt = simpleTypeCodec.decode(b);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			list.add(newStt);
		}

		dto.setList(list);
		
		return dto;
	}

	

	@Override
	public void batchSendMsg(SendMsgDto message,String excludeSessionId, List<String> userIds) {
		for (String userId : userIds) {
			SendMsgDto dto = mapper.map(message, SendMsgDto.class);
			dto.setToId(userId);

			SynMessageTaskDto taskDto = new SynMessageTaskDto();
			taskDto.setSendMsgDto(dto);
			taskDto.setExcludeSessionId(excludeSessionId);
			
			SynMessageTask.addTask(taskDto);
		}
	}

	@Override
	public void pushMsg(MessagePush messagePush, List<String> userIds) {
		long tenementId = messagePush.getTenementId();
		String fromId = messagePush.getFromId();
		long proxyCid = messagePush.getProxyCid();

		for (String userId : userIds) {

			long cid = conversationService.getCid(tenementId, fromId, userId, proxyCid);

			C2sProtocol c2sProtocol = saveOfflineMsg(messagePush, userId);

			// 增加会话未读消息数
			this.conversationService.increaseUnread(messagePush.getType(), userId, cid);

			// 路由协议
			this.protocolRouteService.route(tenementId,userId,JSON.toJSONString(c2sProtocol),null);
		}
	}

	@Override
	public void pushOfflineMsg(long tenementId, String userId, C2sProtocol c2sProtocol) {

		String key = getOfflineSetKey(tenementId, userId);
		// 多设备离线消息
		long msgId = this.getId();
		try {
			saveOfflineMsg(key, msgId, c2sProtocol);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		// 路由协议
		this.protocolRouteService.route(tenementId, userId, JSON.toJSONString(c2sProtocol),null);
	}

	@Override
	public List<C2sProtocol> pullOfflineMsg(OfflineMsgDto offlineMsgDto) {

		return pullOfflineMsgByOvertime(offlineMsgDto).getList();
	}
	
	@Override
	public PullOfflineMsgResultDto pullOfflineMsgByOvertime(OfflineMsgDto offlineMsgDto) {
		
		
		PullOfflineMsgResultDto dto = new PullOfflineMsgResultDto(); 
		//验证参数
		boolean result = Launch.doValidator(offlineMsgDto);
		if (!result) {
			return dto;
		}

		long tenementId = offlineMsgDto.getTenementId();
		String userId = offlineMsgDto.getUserId();
		TenementDo tenement = this.tenementMapper.getTenementById(offlineMsgDto.getTenementId());
		if (tenement == null) {
			return dto;
		}

		String key = getOfflineSetKey(tenementId, userId);
		long lastMsgId = offlineMsgDto.getLastMsgId();

		return pullOfflineMsg(key, lastMsgId);
	}
}