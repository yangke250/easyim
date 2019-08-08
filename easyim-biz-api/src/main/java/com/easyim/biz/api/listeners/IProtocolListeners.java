package com.easyim.biz.api.listeners;


/**
 * im相关的协议处理完以后回调通知
 * @author wl
 *
 * @param <I>
 */
public interface IProtocolListeners<AbstractProtocol,AbstractResultProtocol> {
	
	/**
	 * 事件回调
	 * @param i
	 */
	public void callback(AbstractProtocol input,AbstractResultProtocol output);
	
	/**
	 * 事件回调的消息类型
	 * @return
	 */
	public Class<? extends AbstractProtocol> type();
}