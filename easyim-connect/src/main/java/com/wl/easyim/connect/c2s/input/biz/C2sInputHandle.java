package com.wl.easyim.connect.c2s.input;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ImBizInputHandle extends ByteToMessageDecoder{

	private IProtocolHandleService protocolHandleService;
	
	public ImBizInputHandle(){
		
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> outs) throws Exception {
		for(Object out:outs) {
			
		}
		
		super.callDecode(ctx, in, out);
	}

}
