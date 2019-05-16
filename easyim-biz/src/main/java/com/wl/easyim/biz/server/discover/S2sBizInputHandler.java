package com.wl.easyim.biz.server.discover;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.wl.easyim.biz.api.protocol.s2s.dto.S2sProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class S2sBizInputHandler extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		for(Object o:out){
			String json = (String)o;
			
			S2sProtocol s2sProtocol = JSON.parseObject(json,S2sProtocol.class);
			
			String uuid = s2sProtocol.getUuid();
			
		}
	}

}