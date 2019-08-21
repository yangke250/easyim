package com.easyim.biz.listeners;

import com.easyim.biz.api.dto.protocol.C2sProtocol;

/**
 * 协议back
 * @author wl
 *
 */
public interface ProtocolListener extends EventlListener{
	/**
	 * 处理完相关协议以后额外
	 * @param c2sProtocol
	 */
	public void callback(C2sProtocol c2sProtocol);
}
