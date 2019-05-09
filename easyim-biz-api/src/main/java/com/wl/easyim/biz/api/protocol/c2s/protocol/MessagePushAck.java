package com.wl.easyim.biz.api.protocol.c2s.protocol;

import lombok.Data;

/**
 * 
 * @author wl
 *
 */
@Data
public class MessagePushAck extends AbstractAckProtocol{

	public MessagePushAck(){
		
	}
	
	public MessagePushAck(Result result){
		this.setResult(result);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 2767085791909870818L;

	private long msgId;
	private long cid;
	private boolean read = false;
	
	
}
