package com.wl.easyim.route.server.discover;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.wl.easy.springboot.c2s.client.IC2sClient;
import com.wl.easy.springboot.c2s.dto.ServerDto;
import com.wl.easyim.biz.api.dto.protocol.c2s.C2sProtocol;
import com.wl.easyim.biz.api.dto.protocol.s2s.S2sProtocol;


import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@Slf4j
public class ServerDiscover implements IC2sClient {

	
	private static Map<String,FixedChannelPool> poolMap = new ConcurrentHashMap<String,FixedChannelPool>();
	
	
	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static ReadLock readLock = lock.readLock();
	private static WriteLock writeLock = lock.writeLock();
	
	@PreDestroy
	public void preDestory(){
		for(FixedChannelPool fcp:poolMap.values()){
			if(fcp!=null){
				fcp.close();
			}
		}
	}
	
	
	public static void write(String key,S2sProtocol s2sProtocol){
		try{
			readLock.lock();
			
			FixedChannelPool fcp = poolMap.get(key);
			
			if(fcp==null){
				log.warn("key:{} server is empty",key);
				return;
			}
		}finally{
			readLock.unlock();
		}
	}
	
	
	/**
	 * 
	 * @param ip
	 * @param port
	 */
	private void putPool(String conServer,String ip,int port){
			
			ChannelPoolHandler handler = new ChannelPoolHandler() {
				@Override
				public void channelReleased(Channel ch) throws Exception {
				}

				@Override
				public void channelCreated(Channel ch) throws Exception {
				}

				@Override
				public void channelAcquired(Channel ch) throws Exception {
				}
			};

			EventLoopGroup workerGroup = new NioEventLoopGroup();
			Bootstrap boot = new Bootstrap();
			boot.group(workerGroup).option(ChannelOption.TCP_NODELAY, true).channel(NioServerSocketChannel.class)
					.handler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(new JsonObjectDecoder());
							pipeline.addLast(new S2sBizInputHandler());
						}
					});
			
			boot.connect(ip,port);
			
			FixedChannelPool fcp =  new FixedChannelPool(boot, handler, 2);

			poolMap.put(conServer,fcp);
		
	}
	
	@Override
	public void changeNotify(List<ServerDto> serverDtos) {
		
		try{
			writeLock.lock();
			
			preDestory();
			
			for (ServerDto server : serverDtos) {
				
				String conServer = server.getConnectServer();
				
				String[] str = conServer.split(ServerDto.SPLIT);
				String ip = str[0];
				int port  = Integer.parseInt(str[1]);
				
				putPool(conServer,ip,port);
			}
		}finally{
			writeLock.unlock();
		}
		
	}

	public String path() {
		return "/easy-im/server";
	}

	

}
