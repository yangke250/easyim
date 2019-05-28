package com.wl.easyim.biz.api.protocol.protocol.c2s;

import com.wl.easyim.biz.api.protocol.enums.c2s.ResourceType;

import lombok.Data;

/**
 * 验证身份
 * @author wl
 *
 */
@Data
public class Auth extends AbstractProtocol{

	private static final long serialVersionUID = -6343367293330949158L;
	
	private AuthType authType = AuthType.jwt;
	private String token;//用户登录token
	private long tenementId;//由客户端决定tenmentId
	private int timeOutCycle = 1;//默认60秒超时
	private ResourceType resourceType;
	

	
	
	public static enum AuthType {
		jwt,
		serverToken;
	}
		
}
