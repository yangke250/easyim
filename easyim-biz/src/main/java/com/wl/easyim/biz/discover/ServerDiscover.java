package com.wl.easyim.biz.discover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.springframework.stereotype.Component;

import cn.linkedcare.springboot.sr2f.client.IServerClient;
import cn.linkedcare.springboot.sr2f.dto.ServerDto;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;

@Component
public class ServerDiscover implements IServerClient {

	private Map<String, Bootstrap> map = new ConcurrentHashMap<String, Bootstrap>();

	public static final String split = "#";// 服务分隔符号

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private ReadLock readLock = lock.readLock();

	private WriteLock writeLock = lock.writeLock();

	public void changeNotify(List<ServerDto> serverDtos) {
		Map<String, Bootstrap> map = new ConcurrentHashMap<String, Bootstrap>();

		for (ServerDto server : serverDtos) {

			String key = server.getIp() + split + server.getPort();

		}
	}

	public Bootstrap buildBootstrap(){
		final EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		
		
		Bootstrap boot= new Bootstrap();
		boot.group(workerGroup)
    	.option(ChannelOption.SO_KEEPALIVE, true)W
        .channel(NioServerSocketChannel.class)
        .handler(new ChannelInitializer<Channel>() {

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

	return null;

	}

	public String path() {
		return "/easyim/server";
	}

}
