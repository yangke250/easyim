package com.easyim.biz.domain;

import java.util.Date;

import lombok.Data;

@Data
public class MessageDo {
	private long id;
	private long tenementId;//租户id
	private String fromId;//消息from
	private String toId;//消息接收方
	private String proxyFromId;//发送方路由的代理
	private String proxyToId;//消息接受路由的代理
	private long proxyCid;
	private long cid;
	private int type;//1 文本  
	private String subType;
	private String content;
	private String bizUid;//消息唯一业务码
	private Date gmtCreate;
	
	

}
