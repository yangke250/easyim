package com.wl.easyim.biz.api.protocol.c2s.protocol;

import com.wl.easyim.biz.api.protocol.c2s.enums.ResourceType;
import com.wl.easyim.biz.api.protocol.c2s.enums.Result;

import lombok.Builder;
import lombok.Data;

/**
 * 验证结果返回
 * @author wl
 *
 */
@Data
public class AuthAck extends AbstractAckProtocol{
	private static final long serialVersionUID = -26783567650417475L;
	private ResourceType resource;//多设备登录相关
	private String userId;//用户id
	private long tenementId;//用户租户

	public AuthAck() {
	}
	
	public AuthAck(Result result) {
		super.setResult(result);
	}
	
	
	
}
