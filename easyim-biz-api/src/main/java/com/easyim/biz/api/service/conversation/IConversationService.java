package com.easyim.biz.api.service.conversation;


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
	public void increaseUnread(long cid);
	
	/**
	 * 清空未读消息数
	 * @param tenementId
	 * @param cid
	 */
	public void cleanUnread(long cid);
}
