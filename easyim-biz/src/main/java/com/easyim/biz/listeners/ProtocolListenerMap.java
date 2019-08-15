package com.easyim.biz.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration.BeanPostProcessorsRegistrar;
import org.springframework.stereotype.Component;

import com.easyim.biz.api.listeners.IProtocolListeners;
import com.easyim.biz.api.protocol.c2s.AbstractProtocol;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
@Component
public class EnventListenerMap implements BeanPostProcessor{
	private static Map<Class<? extends AbstractProtocol>,List<IProtocolListeners>> map = 
			new ConcurrentHashMap<Class<? extends AbstractProtocol>,List<IProtocolListeners>>();

	
	public static List<IProtocolListeners> getEnventListener(Class<? extends AbstractProtocol> clazz){
		return map.get(clazz);
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
