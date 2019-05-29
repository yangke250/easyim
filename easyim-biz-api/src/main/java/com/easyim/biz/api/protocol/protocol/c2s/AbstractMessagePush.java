package com.wl.easyim.biz.api.protocol.protocol.c2s;

import lombok.Data;

/**
 * 消息协议的定义
 * @author wl
 *
 */
@Data
public abstract class AbstractMessagePush extends AbstractProtocol{
	private static final long serialVersionUID = 7545645657214366760L;
	
	private long tenementId;
	private long id;
	private int type;
	private int subType;
	private String fromId;
	private String toId;
	private String content;//消息内容
	private String time;//消息时间
	private String bizUuid;//业务uuid
	

	
	

	

}
