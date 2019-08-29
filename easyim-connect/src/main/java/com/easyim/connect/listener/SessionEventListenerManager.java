package com.easyim.connect.listener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionEventListenerManager implements BeanPostProcessor{

	private static List<SessionEventListener> listeners = new CopyOnWriteArrayList<>();
	
	private static List<SessionEventListener> synListeners = new CopyOnWriteArrayList<>();
	
	
	private static LinkedBlockingQueue<SessionEventDto> queue = new LinkedBlockingQueue<SessionEventDto>();
	
	public static void addSessionEventDto(SessionEventDto sessionEventDto){
		queue.add(sessionEventDto);
		
		for(SessionEventListener sl:synListeners){
			sl.callback(sessionEventDto);
		}
	}
	
	@PostConstruct
	public void init(){
		while(true){
			try{
				SessionEventDto sessionEventDto = queue.poll(1, TimeUnit.MINUTES);
				for(SessionEventListener l:listeners){
					l.callback(sessionEventDto);
				}
			}catch(Exception e){
				e.printStackTrace();
				log.error("exception:{}",e);
			}
		}
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof SessionEventListener){
			SessionEventListener l = (SessionEventListener)bean;
			if(l.isSyn()){
				synListeners.add(l);
			}else{
				listeners.add(l);
			}
		}
		return bean;
	}

}
