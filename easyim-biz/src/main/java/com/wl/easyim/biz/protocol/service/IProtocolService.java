package com.wl.easyim.biz.protocol.service;

import com.wl.easyim.biz.api.protocol.c2s.enums.C2sCommandType;
import com.wl.easyim.biz.api.protocol.s2s.dto.S2sProtocol;

/**
 * 协议处理相关类
 * @author wl
 *
 */
public interface IProtocolService {
	
	public C2sCommandType getC2sCommandType();
	
	public S2sProtocol handleProtocol(String uuid,String body,String version);
}
