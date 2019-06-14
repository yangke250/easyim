package com.easyim.route.inputHandler.pool;

import com.easyim.route.inputHandler.S2sClientInputHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class RouteChannelPoolHandler implements ChannelPoolHandler {

	public RouteChannelPoolHandler(){

	}

	@Override
	public void channelCreated(Channel ch) throws Exception {

		
		ChannelPipeline pipeline = ch.pipeline();
		
		pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
		
		pipeline.addLast(new JsonObjectDecoder());
		pipeline.addLast(new S2sClientInputHandler());
	
	}
	
	@Override
	public void channelReleased(Channel ch) throws Exception {
		
	}

	@Override
	public void channelAcquired(Channel ch) throws Exception {
		// TODO Auto-generated method stub
		
	}



}
