package com.easyim.biz.api.protocol.c2s;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import lombok.Data;

/**
 * 消息协议的定义
 * @author wl
 *
 */
@Data
public class MessagePush extends AbstractMessagePush implements Cloneable{

	private static final long serialVersionUID = 7545645657214366760L;
	
    @Protobuf
	private long cid;//会话id

    @Protobuf
    private long proxyCid;//代理会话id
	
	@Override
	public MessagePush clone(){
		try {
			return (MessagePush)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	


	

}
