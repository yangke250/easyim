package com.easyim.biz.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration.BeanPostProcessorsRegistrar;
import org.springframework.stereotype.Component;

import com.easyim.biz.enums.EnventType;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Component
public class EnventListenerMap implements BeanPostProcessor{
	private static Map<EnventType,List<EnventListener>> map = new ConcurrentHashMap<EnventType,List<EnventListener>>();

	
	public static List<EnventListener> getEnventListener(EnventType type){
		return map.get(type);
	}


	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}


	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof EnventListener){
			EnventListener l = (EnventListener)bean;
			
			List<EnventListener> list = map.get(l.type());
			if(list==null){
				list = new ArrayList<EnventListener>();
				map.put(l.type(),list);
			}
				list.add(l);
		}
		
		return bean;
	}
	
}
