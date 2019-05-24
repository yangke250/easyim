package com.wl.easyim.biz.service.conversation;

import com.wl.easyim.biz.domain.ConversationDo;

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
	
}
