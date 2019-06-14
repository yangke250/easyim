package com.easyim.route.inputHandler;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.easyim.biz.api.dto.protocol.S2sProtocol;
import com.easyim.route.inputHandler.pool.RouteFixedChannelPool;
import com.easyim.route.service.impl.ProtocolRouteServiceImpl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S2sClientInputHandler extends ByteToMessageDecoder{


	public S2sClientInputHandler(){

	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		log.info("donoting");
	}

	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		
		super.channelInactive(ctx);
    }
}