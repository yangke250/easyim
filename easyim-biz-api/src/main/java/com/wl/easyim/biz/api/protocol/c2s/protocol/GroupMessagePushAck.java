package com.wl.easyim.biz.api.protocol.c2s.protocol;

import lombok.Data;

@Data
public class GroupMessagePushAck extends AbstractAckProtocol{

	private static final long serialVersionUID = 2434276494963642106L;
	
	
	private long msgId;
	private long groupId;
	private boolean read = false;
	
	
}
