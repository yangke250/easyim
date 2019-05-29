package com.easyim.biz.domain;

import java.util.Date;

import lombok.Data;

@Data
public class ProxyConversationDo {
	private long id;
	private long tenementId;
	private String proxySmallId;
	private String proxyBigId;
	private Date gmtCreate;
}
