package com.easyim.connect.c2s.server;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.easyim.connect.c2s.input.biz.C2sInputBizHandler;
import com.easyim.connect.c2s.input.biz.C2sInputTimeoutHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TcpC2sServer {
	
	@Value("${im.c2s.tcp.port}")
	private int tcpPort;
	
	@Resource
	private C2sInputBizHandler c2sInputBizHandler;
	
	@PostConstruct
	public void initTcpServer() {
		final EventLoopGroup workerGroup = new NioEventLoopGroup();
		final EventLoopGroup bossGroup = new NioEventLoopGroup();
		
		//得到自定义协议相关，解释器
		ServiceLoader<ByteToMessageDecoder> matcher = ServiceLoader.load(ByteToMessageDecoder.class);
		
	    new Thread(()->{
    		ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup)
            	.option(ChannelOption.SO_KEEPALIVE,true)
            	.option(ChannelOption.SO_REUSEADDR,true)
            	.channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                	
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        
                        pipeline.addLast(new C2sInputTimeoutHandler(WebsocketC2sServer.DEFAULT_TIMEOUT));
                        
                        pipeline.addLast(new JsonObjectDecoder());

                        matcher.forEach((ByteToMessageDecoder b)->{
                        	pipeline.addLast(b);
                        });
                        
                        
                        
                        pipeline.addLast(c2sInputBizHandler);
                       
					
                    }
                });

            try {
                Channel ch = boot.bind(tcpPort).sync().channel();
                log.info("===================================");
                log.info("tcp c2s server start at port:"+tcpPort);
                log.info("===================================");
                ch.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally{
               bossGroup.shutdownGracefully();
               workerGroup.shutdownGracefully();
            }
    	}).start();
	}
}
