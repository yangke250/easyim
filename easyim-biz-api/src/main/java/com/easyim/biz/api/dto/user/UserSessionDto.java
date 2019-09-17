package com.easyim.biz.api.dto.user;

import java.io.Serializable;

import com.easyim.biz.api.protocol.enums.c2s.ResourceType;

import lombok.Data;


/**
 * 用户会话session相关数据
 * @author wl
 *
 */
@Data
public class UserSessionDto implements Serializable {

	private static final long serialVersionUID = 641955703820226722L;

	private long tenementId;
	private String userId;
	private ResourceType resourceType;
	
	private String sessionId;
	private String connectServer;
	private int  sessionTimeOut;
	
	
}
