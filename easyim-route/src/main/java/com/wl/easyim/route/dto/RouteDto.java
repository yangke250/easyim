package com.wl.easyim.route.dto;

import com.wl.easyim.biz.api.protocol.c2s.enums.ResourceType;

import lombok.Data;

@Data
public class RouteDto {
	private long tenementId;
	private String userId;
	private ResourceType resourceType;
	
	private String connectServer;
	private String sessionId;
}
