package com.wl.easyim.biz.service.conversation;

import com.wl.easyim.biz.domain.ProxyConversationDo;

/**
 * 代理会话服务
 * @author wl
 *
 */
public interface IProxyConversationService {
	
	/**
	 * 查询会话
	 * @param tenementId
	 * @param fromId
	 * @param toId
	 * @return
	 */
	public long getProxyCid(long tenementId,String proxyFromId,String proxyToId);
	
}
