package com.easyim.biz.api.protocol.s2s;

import lombok.Data;

@Data
public class S2sMessagePush {
	private long tenementId;
	private String toId;
	private String body;
	private String excludeSessionId;
	
}
