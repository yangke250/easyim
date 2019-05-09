package com.wl.easyim.biz.api.protocol.c2s.protocol;

import com.wl.easyim.biz.api.protocol.c2s.dto.ResourceType;

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
	private String authToken;//用户登录token
	private long tenementId;//由客户端决定tenmentId
	private ResourceType resourceType;
	

	
	
	public static enum AuthType {
		jwt,
		serverToken;
	}
		
}
