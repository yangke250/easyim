package com.wl.easyim.biz.service.protocol.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.wl.easyim.biz.api.protocol.c2s.dto.C2sProtocol;
import com.wl.easyim.biz.api.protocol.c2s.enums.C2sCommandType;
import com.wl.easyim.biz.api.protocol.s2s.dto.UserDto;
import com.wl.easyim.biz.api.protocol.service.IC2sHandleService;
import com.wl.easyim.biz.service.protocol.IProtocolService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class C2sHandleServiceImpl implements IC2sHandleService,BeanPostProcessor{

	private Map<C2sCommandType,IProtocolService> map = new HashMap<C2sCommandType,IProtocolService>();
	
	 
	
	@Override
	public C2sProtocol handleProtocol(UserDto userDto,C2sProtocol c2sProtocol,Map<String,String> extendsMap){
		C2sCommandType c2sCommandType = c2sProtocol.getType();
		
		IProtocolService service = map.get(c2sCommandType);
		if(service==null){
			throw new RuntimeException(c2sCommandType+" service is null!");
		}
		
		if(C2sCommandType.ping!=c2sCommandType){
			log.info("handleProtocol:{}",JSON.toJSONString(c2sProtocol));
		}
		
		
		return  service.handleProtocol(userDto,c2sProtocol,extendsMap);
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof IProtocolService){
			IProtocolService service = (IProtocolService)bean;
			C2sCommandType type = service.getC2sCommandType();
			
			map.put(type,service);
		}
		return bean;
	}

}
