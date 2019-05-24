package com.wl.easyim.biz.service.protocol;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wl.easyim.biz.api.dto.protocol.c2s.C2sProtocol;
import com.wl.easyim.biz.api.dto.protocol.s2s.S2sProtocol;
import com.wl.easyim.biz.api.dto.user.UserDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.C2sCommandType;

/**
 * 协议处理相关类
 * @author wl
 *
 */
public interface IC2SProtocolService<I,O> {
	
	/**
	 * 值高的覆盖值的服务
	 * @return
	 */
	default public int order(){
		return 0;
	}

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
	public default C2sProtocol handleProtocol(UserDto userDto,C2sProtocol c2sProtocol,Map<String,String> extendsMap){

		Class<I> entityClass = 
				(Class<I>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]; 


		
		O outputBody = handleProtocolBody(userDto,
				JSON.parseObject(c2sProtocol.getBody(),entityClass),extendsMap);
		
		C2sProtocol c2sProtocolAck = C2sProtocol.builder()
				.type(getC2sCommandType().getAckCommand()).uuid(c2sProtocol.getUuid()).build();
		
		c2sProtocolAck.setBody(JSON.toJSONString(outputBody));
		
		
		return c2sProtocolAck;
	}
	
	/**
	 * 返回相关body
	 * @param uuid
	 * @param body
	 * @param version
	 * @return
	 */
	public O handleProtocolBody(UserDto userDto,I body,Map<String,String> extendsMap);
}
