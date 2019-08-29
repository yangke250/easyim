package com.easyim.connect.listener;

/**
 * session事件回调方法
 * @author wl
 *
 */
public interface SessionEventListener {
	
	/**
	 * 是否同步回调
	 * @return
	 */
	public default boolean isSyn(){
		return false;
	}
	
	/**
	 * session时间回调
	 * @param sessionEventDto
	 */
	public void callback(SessionEventDto sessionEventDto);
}
