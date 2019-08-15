package com.easyim.biz.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration.BeanPostProcessorsRegistrar;
import org.springframework.stereotype.Component;

import com.easyim.biz.api.listeners.IProtocolListeners;
import com.easyim.biz.api.protocol.c2s.AbstractProtocol;
import com.easyim.biz.api.protocol.enums.c2s.C2sCommandType;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
@Component
public class ProtocolListenerMap implements BeanPostProcessor{
	private static Map<C2sCommandType,List<IProtocolListeners>> map = 
			new ConcurrentHashMap<C2sCommandType,List<IProtocolListeners>>();

	
	public static List<IProtocolListeners> getProtocolListener(C2sCommandType c2sCommandType){
		return map.get(c2sCommandType);
	}

	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}


	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof IProtocolListeners){
			IProtocolListeners l = (IProtocolListeners)bean;
			
			List<IProtocolListeners> list = map.get(l.type());
			if(list==null){
				list = new ArrayList<IProtocolListeners>();
				map.put(l.type(),list);
			}
				list.add(l);
		}
		
		return bean;
	}
	
}
