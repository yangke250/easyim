package com.wl.easyim.biz.api.protocol.service;

import com.wl.easyim.biz.api.protocol.c2s.dto.C2sProtocol;
import com.wl.easyim.biz.api.protocol.c2s.protocol.AbstractAckProtocol;

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
	public C2sProtocol handleProtocol(C2sProtocol c2sProtocol);
}
