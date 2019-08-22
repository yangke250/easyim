package com.easyim.biz.api.protocol.enums.c2s;

/**
 * c2s协议类型
 * @author wl
 *
 */
public interface C2sType {
	/**
	 * 得到c2s协议的值
	 * @return
	 */
	default public String getValue() {
		// TODO Auto-generated method stub
		return toString();
	}
	/**
	 * 得到对应的协议回复
	 * @return
	 */
	public  C2sType getAck();
}
