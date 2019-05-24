package com.wl.easyim.biz.api.service.protocol;

import java.util.Map;

import com.wl.easyim.biz.api.dto.protocol.c2s.C2sProtocol;
import com.wl.easyim.biz.api.dto.protocol.s2s.UserDto;
import com.wl.easyim.biz.api.protocol.protocol.c2s.AbstractAckProtocol;

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
