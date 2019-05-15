package com.wl.easyim.biz.protocol.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

import com.wl.easyim.biz.api.protocol.c2s.dto.C2sProtocol;
import com.wl.easyim.biz.api.protocol.c2s.enums.C2sCommandType;
import com.wl.easyim.biz.api.protocol.service.IC2sHandleService;
import com.wl.easyim.biz.protocol.service.IProtocolService;

@Service
public class C2sHandleServiceImpl implements IC2sHandleService,BeanPostProcessor{

	private Map<C2sCommandType,IProtocolService> map = new HashMap<C2sCommandType,IProtocolService>();
	
	@Override
	public C2sProtocol handleProtocol(C2sProtocol c2sProtocol) {
		
		
		return null;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof IProtocolService){
			IProtocolService service = (IProtocolService)bean;
			
			C2sCommandType type = service.getC2sCommandType();
		}
		return bean;
	}

}
