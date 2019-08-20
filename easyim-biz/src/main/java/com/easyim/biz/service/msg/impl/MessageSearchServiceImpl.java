package com.easyim.biz.service.msg.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.dozer.Mapper;

import com.alibaba.dubbo.config.annotation.Service;
import com.easyim.biz.api.dto.message.SearchMsgCidDto;
import com.easyim.biz.api.protocol.c2s.MessagePush;
import com.easyim.biz.api.service.message.IMessageSearchService;
import com.easyim.biz.api.service.protocol.IC2sHandleService;
import com.easyim.biz.domain.MessageDo;
import com.easyim.biz.mapper.conversation.IProxyConversationMapper;
import com.easyim.biz.mapper.message.IMessageMapper;

/**
 * 消息查询服务
 * @author wl
 *
 */
@Service(interfaceClass=IMessageSearchService.class)
public class MessageSearchServiceImpl implements IMessageSearchService{

	
	@Resource
	private IMessageMapper messageMapper;

	@Resource
	private IProxyConversationMapper proxyConversationMapper;

	@Resource
	private Mapper mapper;
	
	@Override
	public List<MessagePush> selectMsgHistory(long tenementId,long proxyCid, long minMsgId) {

		return selectMsgHistory(tenementId,proxyCid,0l,minMsgId);
	}

	/**
	 * 查询历史消息
	 * @param tenementId
	 * @param proxyCid
	 * @param cid
	 * @param minMsgId
	 * @return
	 */
	private List<MessagePush> selectMsgHistory(long tenementId,long proxyCid, long cid, long minMsgId){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		List<MessagePush> messages = new ArrayList<MessagePush>();
		
		List<MessageDo> list =  this.messageMapper.selectMessage(tenementId, proxyCid,cid,minMsgId);
		for(MessageDo l:list){
			MessagePush messagePush = mapper.map(l,MessagePush.class);
			messagePush.setTime(sdf.format(l.getGmtCreate()));
			
			messages.add(messagePush);
		}
		
		return messages;
	}


	@Override
	public List<MessagePush> selectMsgHistoryBySelf(long tenementId,long proxyCid, long cid, long minMsgId) {
		return selectMsgHistory(tenementId,proxyCid,cid,minMsgId);
	}

	

}
