package com.easyim.biz.api.listeners;

import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.enums.c2s.C2sType;


/**
 * im相关的协议处理完以后回调通知
 * @author wl
 *
 * @param <I>
 */
public interface IProtocolListeners<AbstractProtocol,AbstractResultProtocol> {
	
	/**
	 * 是否同步调用
	 * @return
	 */
	public default boolean isSyn(){
		return false;
	}
	
	/**
	 * 事件回调
	 * @param i
	 */
	public void callback(UserSessionDto userSessionDto,String c2sType,AbstractProtocol input,AbstractResultProtocol output);
	
	/**
	 * 事件回调的消息类型
	 * @return
	 */
	public C2sType type();
}
