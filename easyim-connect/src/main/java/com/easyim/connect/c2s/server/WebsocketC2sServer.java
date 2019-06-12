package com.easyim.connect.c2s.server;

import java.util.ServiceLoader;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.easyim.connect.c2s.input.C2sInputBizHandler;
import com.easyim.connect.c2s.input.C2sInputTimeoutHandler;
import com.easyim.connect.c2s.input.WebSocketHandler;
import com.easyim.connect.c2s.output.WenSocketOutputHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebsocketC2sServer {

	@Value("${c2s.websocket.port}")
	private int websocketPort;
	
	@Resource
	private C2sInputBizHandler c2sInputHandle;
	
	public static final int DEFAULT_TIMEOUT=60;
	
	@PostConstruct
	public void initTcpServer() {
		final EventLoopGroup workerGroup = new NioEventLoopGroup();
		final EventLoopGroup bossGroup = new NioEventLoopGroup();
		
		//得到自定义协议相关，解释器
		ServiceLoader<ByteToMessageDecoder> matcher = ServiceLoader.load(ByteToMessageDecoder.class);
				
		
	    new Thread(()->{
    		ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup)
            	.channel(NioServerSocketChannel.class)
            	.option(ChannelOption.SO_REUSEADDR,true)
                .childHandler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        
                        pipeline.addLast(new C2sInputTimeoutHandler(DEFAULT_TIMEOUT));
                        
                        pipeline.addLast("http-codec",new HttpServerCodec());
                        pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
                        pipeline.addLast("http-chunked",new ChunkedWriteHandler());
                        pipeline.addLast(new WebSocketHandler());       
                    
                        matcher.forEach((ByteToMessageDecoder b)->{
                        	pipeline.addLast(b);
                        });
                        
                        pipeline.addLast(c2sInputHandle);
                        
                        pipeline.addLast(new WenSocketOutputHandler());
                    }
                });

            try {
                Channel ch = boot.bind(websocketPort).sync().channel();
                log.info("===================================");
                log.info("websocket c2s server start at port:"+websocketPort);
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
