package com.wl.easyim.biz.service.msg.impl;

import java.util.List;

import com.wl.easyim.biz.api.protocol.protocol.c2s.MessagePush;
import com.wl.easyim.biz.api.service.message.IMessageSearchService;

/**
 * 消息查询服务
 * @author wl
 *
 */
public class MessageSearchServiceImpl implements IMessageSearchService{

	@Override
	public List<MessagePush> selectReverseMsg(long cid, long minMsgId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MessagePush> selectReverseMsgByProxyCid(long proxyCid, long minMsgId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessagePush getMsgByBizId(String bizUuid) {
		// TODO Auto-generated method stub
		return null;
	}

}
