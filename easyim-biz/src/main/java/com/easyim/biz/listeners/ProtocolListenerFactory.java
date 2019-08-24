package com.easyim.biz.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration.BeanPostProcessorsRegistrar;
import org.springframework.stereotype.Component;

import com.easyim.biz.api.listeners.IProtocolListeners;
import com.easyim.biz.api.protocol.c2s.AbstractProtocol;
import com.easyim.biz.api.protocol.enums.c2s.C2sType;
import com.easyim.biz.api.protocol.enums.c2s.EasyImC2sType;
import com.easyim.biz.listeners.dto.ProtocolListenerDto;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
@Component
public class ProtocolListenerFactory implements BeanPostProcessor {
	private static Map<C2sType, List<IProtocolListeners>> map = new ConcurrentHashMap<C2sType, List<IProtocolListeners>>();

	private static LinkedBlockingQueue<ProtocolListenerDto> queue = new LinkedBlockingQueue<ProtocolListenerDto>();

	private List<IProtocolListeners> getProtocolListener(C2sType c2sCommandType) {
		return map.get(c2sCommandType);
	}

	public static void addProtocolCallback(ProtocolListenerDto dto) {
		queue.add(dto);

	}

	public void init() {
		while (true) {
			ProtocolListenerDto dto = null;
			try {
				dto = queue.poll(1, TimeUnit.HOURS);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (dto == null) {
				continue;
			}

			List<IProtocolListeners> list = map.get(dto.getC2sType());
			if (list == null) {
				continue;
			}

			try {
				for (IProtocolListeners l : list) {
					l.callback(dto.getUserSessionDto(),dto.getC2sType(),dto.getInput(),dto.getOutput());
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("exception:{}", e);
			}

		}
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof IProtocolListeners) {
			IProtocolListeners l = (IProtocolListeners) bean;

			List<IProtocolListeners> list = map.get(l.type());
			if (list == null) {
				list = new ArrayList<IProtocolListeners>();
				map.put(l.type(), list);
			}
			list.add(l);
		}

		return bean;
	}

}
