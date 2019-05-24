package com.wl.easyim.biz.api.protocol.protocol.c2s;

import lombok.Data;

@Data
public class GroupMessagePush extends AbstractMessagePush{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8449620967796682943L;
	private long groupId;
	

}
