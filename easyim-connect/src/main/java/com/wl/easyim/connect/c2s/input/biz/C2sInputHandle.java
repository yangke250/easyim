package com.wl.easyim.connect.c2s.input.biz;

import java.util.List;

import com.wl.easyim.biz.api.protocol.service.IC2sHandleService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author wl
 *
 */
public class C2sInputHandle extends AbstractC2sInputHandle{

	private IC2sHandleService c2sHandleService;
	
	public C2sInputHandle(IC2sHandleService c2sHandleService){
		this.c2sHandleService = c2sHandleService;
	}
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> outs) throws Exception {
		super.channelActive(ctx);
		for(Object out:outs) {
			
		}
	}

}
