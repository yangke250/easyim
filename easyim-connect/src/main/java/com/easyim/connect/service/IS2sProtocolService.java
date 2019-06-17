package com.easyim.connect.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.alibaba.fastjson.JSON;
import com.easyim.biz.api.dto.protocol.S2sProtocol;
import com.easyim.biz.api.protocol.enums.s2s.S2sCommandType;

public interface IS2sProtocolService<I,O> {
	
	
	public default O handleProtocolBody(String body){
		ParameterizedType parameterizedType=(ParameterizedType)this.getClass().getGenericInterfaces()[0];
		Type type = parameterizedType.getActualTypeArguments()[0];
		Class<I> classInput = null;
		try {
			classInput = (Class<I>) Class.forName(type.getTypeName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		I input =JSON.parseObject(body,classInput);
		
		return handleProtocolBody(input);
	}
	
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
