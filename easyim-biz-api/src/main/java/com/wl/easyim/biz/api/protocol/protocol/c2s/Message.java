package com.wl.easyim.biz.api.protocol.protocol.c2s;

import com.wl.easyim.biz.api.dto.message.MessageSendDto.MessageType;

import lombok.Data;

@Data
public class Message extends AbstractProtocol{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long tenementId;
	private long cid;
	private long proxyCid;
	
	private String fromId;
	private String fromProxyId;
	private String toId;
	private String toProxyId;
	
	private MessageType type;
	private int subType;
	private String content;
	
}