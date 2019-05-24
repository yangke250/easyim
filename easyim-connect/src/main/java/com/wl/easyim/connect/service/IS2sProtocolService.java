package com.wl.easyim.connect.service;

import com.wl.easyim.biz.api.dto.protocol.s2s.S2sProtocol;
import com.wl.easyim.biz.api.protocol.enums.s2s.S2sCommandType;

public interface IS2sProtocolService<I,O> {
	
	
	/**
	 * 
	 * @param body
	 * @return
	 */
	public O handleProtocolBody(I body);
	
	/**
	 * 同一类型值高的覆盖值低的接口
	 * @return
	 */
	default public int order(){
		return 0;
	}
	
	public S2sCommandType getType();
	
}
