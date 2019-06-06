package com.easyim.biz.api.protocol.c2s;

import lombok.Data;

@Data
public class GroupMessagePushAck extends AbstractResultProtocol{

	private static final long serialVersionUID = 2434276494963642106L;
	
	
	private long msgId;
	private long groupId;
	private boolean read = false;
	
	
}
