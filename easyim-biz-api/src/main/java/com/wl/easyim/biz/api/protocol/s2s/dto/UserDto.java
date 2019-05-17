package com.wl.easyim.biz.api.protocol.s2s.dto;

import java.io.Serializable;

import com.wl.easyim.biz.api.protocol.c2s.enums.ResourceType;

import lombok.Data;

@Data
public class UserDto implements Serializable {

	private static final long serialVersionUID = 641955703820226722L;

	private long tenementId;
	private String userId;
	private ResourceType resourceType;
	
	private String sessionId;
	private String connectServer;
	private int  sessionTimeOut;
	
}
