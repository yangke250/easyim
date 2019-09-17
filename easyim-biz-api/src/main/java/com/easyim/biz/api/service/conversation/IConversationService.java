package com.easyim.biz.api.service.conversation;

import java.util.List;

import com.easyim.biz.api.dto.conversation.ConversationDto;
import com.easyim.biz.api.dto.conversation.UnreadDto;
import com.easyim.biz.api.protocol.c2s.MessagePush;

/**
 * 会话服务
 * @author wl
 *
 */
public interface IConversationService {
	
	/**
	 * 查询会话
	 * @param tenementId
	 * @param fromId
	 * @param toId
	 * @return
	 */
	public long getCid(long tenementId,String fromId,String toId,long proxyCid);
	
	/**
	 * 增加未读消息数
	 * @param tenementId
	 * @param cid
	 */
	public void increaseUnread(int msgType,String userId,long cid);
	
	/**
	 * 清空未读消息数
	 * @param tenementId
	 * @param cid
	 */
	public void cleanUnread(String userId,long cid);
	
	/**
	 * 得到会话的未读消息数
	 * @param userId
	 * @param cids
	 */
	public List<UnreadDto> selectUnread(String userId,List<Long> cids);
	
	/**
	 * 添加最近的聊天会话
	 * @param messagePush
	 */
	public void addRecentlyConversation(MessagePush messagePush);
	
	/**
	 * 查询一个用户的最近聊天记录
	 * @param tenementId
	 * @param userId
	 * @return
	 */
	public List<ConversationDto> selectRecentlyConversation(long tenementId,String userId);
}
