package com.easyim.biz.api.listener;

import com.easyim.biz.api.protocol.c2s.MessagePush;
import com.easyim.biz.api.protocol.enums.c2s.EnventType;

/**
 * 
 * @author wl
 *
 */
public interface SendMsgListener extends EnventListener<MessagePush> {

	public default  EnventType type(){
		return EnventType.sendMsg;
	};
}
