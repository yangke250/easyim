package com.wl.easyim.biz.api.protocol.c2s.protocol;


/**
 * 消息协议的定义
 * @author wl
 *
 */
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
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getToId() {
		return toId;
	}

	public void setToId(String toId) {
		this.toId = toId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSubType() {
		return subType;
	}

	public void setSubType(int subType) {
		this.subType = subType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTenementId() {
		return tenementId;
	}

	public void setTenementId(long tenementId) {
		this.tenementId = tenementId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public String getBizUuid() {
		return bizUuid;
	}

	public void setBizUuid(String bizUuid) {
		this.bizUuid = bizUuid;
	}

	
	

	

}
