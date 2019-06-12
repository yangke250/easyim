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
	 * 逆向查询消息历史
	 * @param cid
	 * @param minMsgId
	 * @return
	 */
	public List<MessagePush> selectReverseMsg(SearchMsgCidDto searchMsgCidDto);

	/**
	 * 逆向查询消息历史
	 * @param cid
	 * @param minMsgId
	 * @return
	 */
	public List<MessagePush> selectReverseMsgByProxyCid(long proxyCid,String userId,long minMsgId);
	
	/**
	 * 逆向查询消息历史
	 * @param cid
	 * @param minMsgId
	 * @return
	 */
	public List<MessagePush> selectReverseMsgByFromToId(long proxyCid,long minMsgId);
	
	
	/**
	 * 根据biz id查询消息
	 * @param bizUuid
	 * @return
	 */
	public MessagePush getMsgByBizId(String bizUuid);

	
}
