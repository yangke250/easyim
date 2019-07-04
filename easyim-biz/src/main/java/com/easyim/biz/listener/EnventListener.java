package com.easyim.biz.listener;

import com.easyim.biz.enums.EnventType;

/**
 * im相关的事件通知
 * @author wl
 *
 * @param <I>
 */
public interface EnventListener<I> {
	/**
	 * 事件回调
	 * @param i
	 */
	public void callback(I i);
	
	public EnventType type();
}
