package com.easyim.biz.api.protocol.protocol.c2s;

import lombok.Data;

/**
 * 消息协议的定义
 * @author wl
 *
 */
@Data
public class MessagePush extends AbstractMessagePush implements Cloneable{

	private static final long serialVersionUID = 7545645657214366760L;
	
	private long cid;//会话id

	
	@Override
	public MessagePush clone(){
		try {
			return (MessagePush)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	

	

}
