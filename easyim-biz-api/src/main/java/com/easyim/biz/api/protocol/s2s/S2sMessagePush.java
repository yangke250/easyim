package com.easyim.biz.api.protocol.protocol.s2s;

import lombok.Data;

@Data
public class S2sMessagePush {
	private long tenementId;
	private String toId;
	private String body;
}
