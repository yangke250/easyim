package com.wl.easyim.connect.c2s.server;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TcpC2sServer {
	
	@Value("${im.c2s.tcp.port}")
	private int tcpPort;
	
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

                        pipeline.addLast("JsonObjectDecoder",new JsonObjectDecoder());

                        matcher.forEach((ByteToMessageDecoder b)->{
                        	pipeline.addLast(UUID.randomUUID().toString(),b);
                        });
                    }
                });

            try {
                Channel ch = boot.bind(tcpPort).sync().channel();
                System.out.println("tcp server start at port:"+tcpPort);
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
