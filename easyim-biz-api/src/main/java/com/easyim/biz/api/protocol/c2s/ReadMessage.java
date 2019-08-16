package com.easyim.biz.api.protocol.c2s;

import lombok.Data;

@Data
public class ReadMessage  extends AbstractProtocol{
	private long cid;
}
