package com.easyim.connect.s2s.input;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.easy.springboot.c2s.server.AbstractServerRegister;
import com.easyim.biz.api.dto.protocol.S2sProtocol;
import com.easyim.biz.api.protocol.enums.s2s.S2sCommandType;
import com.easyim.connect.service.IS2sProtocolService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author wl
 *
 */
@Component
@Sharable
@Slf4j
public class S2sInputHandle extends ChannelInboundHandlerAdapter implements BeanPostProcessor{

	private Map<S2sCommandType,IS2sProtocolService<?,?>> map = new ConcurrentHashMap<S2sCommandType,IS2sProtocolService<?,?>>();
	
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		log.info("S2sInputHandle:{}",msg.toString());
		
		S2sProtocol s2sProtocol = JSON.parseObject(msg.toString(),S2sProtocol.class);
		if(!AbstractServerRegister.getPassword().equals(s2sProtocol.getPassword())){
			log.error("S2sInputHandle password error:{},{}",AbstractServerRegister.getPassword(),s2sProtocol.getPassword());
		}
		
		IS2sProtocolService<?,?> service =  map.get(s2sProtocol.getType());
		service.handleProtocolBody(s2sProtocol.getBody());
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
