package com.easyim.connect.listener;

import com.easyim.biz.api.protocol.enums.c2s.ResourceType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionEventDto {
	public static enum SessionEvent{
		anonymousLogin,//匿名
		authLogin,//认证登录
		logout;//已登录
	}
	
	private SessionEvent sessionEvent;
	private long tenementId;
	private String userId;
	private ResourceType resource;//设备
	
}
