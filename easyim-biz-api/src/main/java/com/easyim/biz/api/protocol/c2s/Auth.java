package com.easyim.biz.api.protocol.c2s;

import com.easyim.biz.api.protocol.enums.c2s.ResourceType;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
	private int timeoutCycle = 1;//默认60秒超时
	
	public static final int MAX_CYCLE = 15;
	
	
	public static enum AuthType {
		jwt,
		serverToken;
	}
	
	
	public void setTimeoutCycle(int timeoutCycle){
		this.timeoutCycle = timeoutCycle;
		if(this.timeoutCycle>MAX_CYCLE){
			this.timeoutCycle = MAX_CYCLE;
		}
	}
		
}
