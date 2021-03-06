package com.easyim.biz.service.protocol;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.easyim.biz.api.dto.protocol.C2sProtocol;
import com.easyim.biz.api.dto.protocol.S2sProtocol;
import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.c2s.AbstractProtocol;
import com.easyim.biz.api.protocol.enums.c2s.C2sCommandType;

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
	public default C2sProtocol handleProtocol(UserSessionDto userSessionDto,C2sProtocol c2sProtocol,Map<String,String> extendsMap){

		ParameterizedType parameterizedType=(ParameterizedType)this.getClass().getGenericInterfaces()[0];
		Type type = parameterizedType.getActualTypeArguments()[0];
		Class<I> classInput = null;
		try {
			classInput = (Class<I>) Class.forName(type.getTypeName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		I input =JSON.parseObject(c2sProtocol.getBody(),classInput);
		
		O outputBody = handleProtocolBody(userSessionDto,
				input,extendsMap);
		
		C2sProtocol c2sProtocolAck = new C2sProtocol(getC2sCommandType().getAckCommand(),
				JSON.toJSONString(outputBody));
		c2sProtocolAck.setUuid(c2sProtocol.getUuid());
		
		return c2sProtocolAck;
	}
	
	/**
	 * 返回相关body
	 * @param uuid
	 * @param body
	 * @param version
	 * @return
	 */
	public O handleProtocolBody(UserSessionDto userSessionDto,I body,Map<String,String> extendsMap);
}
