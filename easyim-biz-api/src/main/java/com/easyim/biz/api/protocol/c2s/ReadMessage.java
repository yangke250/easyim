package com.easyim.biz.api.protocol.c2s;

import lombok.Data;

@Data
public class ReadMessage  extends AbstractProtocol{
	private long cid;//会话id
	private long msgId;//已读最后一天消息id
	
}
