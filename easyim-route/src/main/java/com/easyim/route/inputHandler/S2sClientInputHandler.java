package com.easyim.route.inputHandler;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.easyim.biz.api.dto.protocol.S2sProtocol;
import com.easyim.route.service.impl.ProtocolRouteServiceImpl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class S2sClientInputHandler extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		for(Object o:out){
			String json = (String)o;
			
			ProtocolRouteServiceImpl.addCallBack(json);
		}
	}

}