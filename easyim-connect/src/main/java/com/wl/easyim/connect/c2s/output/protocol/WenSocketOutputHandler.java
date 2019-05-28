package com.wl.easyim.connect.c2s.output.protocol;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * websocket 协议输出
 * @author wl
 *
 */
public class WenSocketOutputHandler extends ChannelOutboundHandlerAdapter{

	 @Override
	 public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		 if(msg instanceof String){
			 String str = (String)msg;
			 
			 super.write(ctx,new TextWebSocketFrame(str), promise);
		 }else{
	    	 super.write(ctx,msg,promise);
		 }
	 }
	 
}
