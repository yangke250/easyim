package com.wl.easyim.biz.api.protocol.c2s.protocol;

import com.wl.easyim.biz.api.protocol.c2s.dto.ResourceType;

/**
 * 验证结果返回
 * @author wl
 *
 */

public class AuthAck extends AbstractAckProtocol{
	private static final long serialVersionUID = -26783567650417475L;
	private ResourceType resource;//多设备登录相关
	private String userId;//用户id
	private long tenementId;//用户租户
	private String token;//访问权限token
	private String proxyId;//代理id

	public AuthAck() {
	}
	
	public AuthAck(Result result) {
		super.setResult(result);
	}
	
	public ResourceType getResource() {
		return resource;
	}
	public void setResource(ResourceType resource) {
		this.resource = resource;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getTenementId() {
		return tenementId;
	}
	public void setTenementId(long tenementId) {
		this.tenementId = tenementId;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public String getProxyId() {
		return proxyId;
	}

	public void setProxyId(String proxyId) {
		this.proxyId = proxyId;
	}
	
}
