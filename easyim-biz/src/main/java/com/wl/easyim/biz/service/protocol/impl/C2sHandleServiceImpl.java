package com.wl.easyim.biz.service.protocol.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.wl.easyim.biz.api.dto.protocol.c2s.C2sProtocol;
import com.wl.easyim.biz.api.dto.user.UserDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.wl.easyim.biz.api.service.protocol.IC2sHandleService;
import com.wl.easyim.biz.service.protocol.IC2SProtocolService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class C2sHandleServiceImpl implements IC2sHandleService,BeanPostProcessor{

	private Map<C2sCommandType,IC2SProtocolService> map = new HashMap<C2sCommandType,IC2SProtocolService>();
	
	 
	
	@Override
	public C2sProtocol handleProtocol(UserDto userDto,C2sProtocol c2sProtocol,Map<String,String> extendsMap){
		C2sCommandType c2sCommandType = c2sProtocol.getType();
		
		IC2SProtocolService service = map.get(c2sCommandType);
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
		if(bean instanceof IC2SProtocolService){
			IC2SProtocolService service = (IC2SProtocolService)bean;
			C2sCommandType type = service.getC2sCommandType();
			
			IC2SProtocolService oldService = map.get(type);
			
			if(oldService==null||service.order()>oldService.order()){
				log.info("IC2SProtocolService : {} overwrite",oldService.getClass().getName());
				map.put(type,service);
			}else{
				log.info("IC2SProtocolService : {} low",service.getClass().getName());
			}
			
		}
		return bean;
	}

}
