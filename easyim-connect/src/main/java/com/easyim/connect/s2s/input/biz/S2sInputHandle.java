package com.wl.easyim.connect.s2s.input.biz;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.wl.easyim.biz.api.dto.protocol.s2s.S2sProtocol;
import com.wl.easyim.biz.api.protocol.enums.s2s.S2sCommandType;
import com.wl.easyim.connect.service.IS2sProtocolService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * @author wl
 *
 */
@Component
public class S2sInputHandle extends ChannelInboundHandlerAdapter implements BeanPostProcessor{

	private Map<S2sCommandType,IS2sProtocolService> map = new ConcurrentHashMap<S2sCommandType,IS2sProtocolService>();
	
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		S2sProtocol s2sProtocol = JSON.parseObject(msg.toString(),S2sProtocol.class);
		
		switch(s2sProtocol.getType()){
			case s2sMessagePush:
		}
		
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof IS2sProtocolService){
			IS2sProtocolService service = (IS2sProtocolService)bean;
			
			IS2sProtocolService oldService = map.get(service.getType());
			if(oldService==null||service.order()>oldService.order()){
				map.put(service.getType(),service);
			}
		}
		
		return bean;
	}
}
