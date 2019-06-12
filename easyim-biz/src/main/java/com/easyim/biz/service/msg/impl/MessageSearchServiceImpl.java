package com.easyim.biz.service.msg.impl;

import java.util.List;

import com.alibaba.dubbo.config.annotation.Service;
import com.easyim.biz.api.dto.message.SearchMsgCidDto;
import com.easyim.biz.api.protocol.c2s.MessagePush;
import com.easyim.biz.api.service.message.IMessageSearchService;
import com.easyim.biz.api.service.protocol.IC2sHandleService;

/**
 * 消息查询服务
 * @author wl
 *
 */
@Service(interfaceClass=IMessageSearchService.class)
public class MessageSearchServiceImpl implements IMessageSearchService{

	
	
	@Override
	public List<MessagePush> selectReverseMsg(SearchMsgCidDto searchMsgCidDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MessagePush> selectReverseMsgByProxyCid(long proxyCid, String userId, long minMsgId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MessagePush> selectReverseMsgByFromToId(long proxyCid, long minMsgId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessagePush getMsgByBizId(String bizUuid) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
