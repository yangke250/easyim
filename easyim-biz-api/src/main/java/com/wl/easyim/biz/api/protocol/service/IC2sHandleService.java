package com.wl.easyim.biz.api.protocol.service;

import java.util.Map;

import com.wl.easyim.biz.api.protocol.c2s.dto.C2sProtocol;
import com.wl.easyim.biz.api.protocol.c2s.protocol.AbstractAckProtocol;
import com.wl.easyim.biz.api.protocol.s2s.dto.UserDto;

/**
 * 所有相关协议入库
 * @author wl
 *
 */
public interface IC2sHandleService {

	/**
	 * 处理相关协议
	 * @param c2sProtocol
	 * @return
	 */
	public C2sProtocol handleProtocol(UserDto userDto,C2sProtocol c2sProtocol,Map<String,String> extendsMap);
}
