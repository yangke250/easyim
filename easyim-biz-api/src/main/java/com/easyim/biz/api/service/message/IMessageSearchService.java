package com.easyim.biz.api.service.message;

import java.util.List;

import com.easyim.biz.api.dto.message.SearchMsgCidDto;
import com.easyim.biz.api.protocol.c2s.MessagePush;

/**
 * 消息查询服务
 * @author wl
 *
 */
public interface IMessageSearchService {

	/**
	 * 查询消息历史
	 * @param cid
	 * @param minMsgId
	 * @return
	 */
	public List<MessagePush> selectMsgHistory(long tenementId,long proxyCid,long minMsgId);
	
	/**
	 * 查询消息历史,只显示自己的对话
	 * @param cid
	 * @param minMsgId
	 * @return
	 */
	public List<MessagePush> selectMsgHistoryBySelf(long tenementId,long proxyCid,long cid,long minMsgId);
	
	

	
}
