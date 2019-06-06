package com.easyim.biz.api.protocol.protocol.c2s;

import com.easyim.biz.api.protocol.enums.c2s.Result;

import lombok.Data;

/**
 * 
 * @author wl
 *
 */
@Data
public class MessagePushAck extends AbstractResultProtocol{

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
