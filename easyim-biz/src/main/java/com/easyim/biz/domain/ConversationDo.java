package com.easyim.biz.domain;

import java.util.Date;

import lombok.Data;

/**
 * 会话对象
 * @author wl
 *
 */
@Data
public class ConversationDo {
	private long id;
	private long tenementId;
	private String smallId;
	private String bigId;
	private long proxyCid;
	private Date gmtCreate;
	
}
