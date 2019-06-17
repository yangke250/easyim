package com.easyim.connect.c2s.input;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyim.connect.session.SessionManager;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.Attribute;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Sharable
@Slf4j
public class WebSocketHandler  extends SimpleChannelInboundHandler<Object>{

    private  WebSocketServerHandshaker handshaker;
    
    

    
    

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg)
            throws Exception {
    	if(msg instanceof FullHttpRequest){//HTTP接入，WebSocket第一次连接使用HTTP连接,用于握手
            handleHttpRequest(ctx, (FullHttpRequest)msg);
        }else if(msg instanceof WebSocketFrame){//Websocket 接入
        	
        	WebSocketFrame frame = (WebSocketFrame)msg;
        	
        	 /**
             * 判断是否ping消息
             */
            if (frame instanceof PingWebSocketFrame) {
            	log.info("服务端收到 ping：" + frame.content());
                ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
                return;
            }else if (frame instanceof BinaryWebSocketFrame) {//不支持二进制消息
                throw new UnsupportedOperationException(String.format(
                        "%s frame types not supported", frame.getClass().getName()));
            }else if (frame instanceof TextWebSocketFrame){
                // 返回应答消息
                String request = ((TextWebSocketFrame) frame).text();
                //logger.info("服务端收到：" + request);
                
                ctx.fireChannelRead(request);
             }else if (frame instanceof CloseWebSocketFrame){
                 
                 SessionManager.removeSession(ctx, null);
              }else{
             	log.error("服务端收到 ：" + frame.content());
            }
        	
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	log.error("channelActive:{}",ctx);
    	super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	log.error("exceptionCaught:{}",cause);
        ctx.close();
    }


    private void handleHttpRequest(ChannelHandlerContext ctx,FullHttpRequest req){
        if (!req.getDecoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:1024/ws", null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx,
            FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static boolean isKeepAlive(FullHttpRequest req) {
       return false;
    }

    
}

