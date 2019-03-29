package com.wl.easyim.connect.c2s.server;

import javax.annotation.PostConstruct;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebsocketServer {

	@PostConstruct
	public void initTcpServer() {
		final EventLoopGroup workerGroup = new NioEventLoopGroup();
		final EventLoopGroup bossGroup = new NioEventLoopGroup();
		
	    new Thread(()->{
    		ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        
                        pipeline.addLast("http-codec",new HttpServerCodec());
                        pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
                        pipeline.addLast("http-chunked",new ChunkedWriteHandler());
                        pipeline.addLast("handler",new WebSocketHandler(protocolDispatchService));       
                    }

                });

            try {
                Channel ch = boot.bind(port).sync().channel();
                System.out.println("websocket server start at port:1024");
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
