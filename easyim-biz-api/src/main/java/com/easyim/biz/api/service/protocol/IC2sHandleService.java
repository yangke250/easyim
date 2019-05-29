package com.easyim.biz.api.service.protocol;

import java.util.Map;

import com.easyim.biz.api.dto.protocol.c2s.C2sProtocol;
import com.easyim.biz.api.dto.user.UserSessionDto;

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
	public C2sProtocol handleProtocol(UserSessionDto userSessionDto,C2sProtocol c2sProtocol,Map<String,String> extendsMap);
}
