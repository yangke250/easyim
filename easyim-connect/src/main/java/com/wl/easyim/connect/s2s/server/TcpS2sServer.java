package com.wl.easyim.connect.s2s.server;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.wl.easyim.connect.c2s.input.protocol.WebSocketHandler;

import cn.linkedcare.springboot.sr2f.server.ZkServerRegister;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TcpS2sServer {


	@Value("${im.s2s.tcp.port}")
	private int tcpPort;
	
	@PostConstruct
	public void initTcpServer() {
		final EventLoopGroup workerGroup = new NioEventLoopGroup();
		final EventLoopGroup bossGroup = new NioEventLoopGroup();
		
	    new Thread(()->{
    		ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup)
            	.option(ChannelOption.SO_KEEPALIVE, true)
            	.option(ChannelOption.SO_REUSEADDR,true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        
                        pipeline.addLast("JsonObjectDecoder",new JsonObjectDecoder());

                        
                    }

                });

            try {
                Channel ch = boot.bind(tcpPort).sync().channel();
                log.info("websocket server start at port:",tcpPort);
                
                doRegister();
                ch.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally{
               bossGroup.shutdownGracefully();
               workerGroup.shutdownGracefully();
            }
    	}).start();
	}
	
	private void doRegister(){
		ZkServerRegister zkServerRegister = new ZkServerRegister("/easyim/server",tcpPort);
	}

}
