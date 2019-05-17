package com.wl.easyim.biz.service.protocol;

import java.util.Map;

import com.wl.easyim.biz.api.protocol.c2s.dto.C2sProtocol;
import com.wl.easyim.biz.api.protocol.c2s.enums.C2sCommandType;
import com.wl.easyim.biz.api.protocol.s2s.dto.S2sProtocol;
import com.wl.easyim.biz.api.protocol.s2s.dto.UserDto;

/**
 * 协议处理相关类
 * @author wl
 *
 */
public interface IProtocolService {
	
	/**
	 * 协议类型
	 * @return
	 */
	public C2sCommandType getC2sCommandType();
	
	/**
	 * 返回相关body
	 * @param uuid
	 * @param body
	 * @param version
	 * @return
	 */
	public C2sProtocol handleProtocol(UserDto userDto,C2sProtocol c2sProtocol,Map<String,String> extendsMap);
}
