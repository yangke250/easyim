package com.easyim.route.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.easy.springboot.c2s.client.IC2sClient;
import com.easy.springboot.c2s.dto.ServerDto;
import com.easyim.biz.api.dto.protocol.C2sProtocol;
import com.easyim.biz.api.dto.protocol.S2sProtocol;
import com.easyim.route.inputHandler.S2sClientInputHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
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
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@Slf4j
public class ServerDiscover implements IC2sClient {

	private static Map<String, FixedChannelPool> connectPoolMap = new ConcurrentHashMap<String, FixedChannelPool>();

	private static List<String> connectList = new CopyOnWriteArrayList<String>();

	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static ReadLock readLock = lock.readLock();
	private static WriteLock writeLock = lock.writeLock();

	private static AtomicLong POLLING_NUM = new AtomicLong();

	@PreDestroy
	public void preDestory() {
		for (FixedChannelPool fcp : connectPoolMap.values()) {
			if (fcp != null) {
				fcp.close();
			}
		}

		connectList.clear();
	}

	
	
	/**
	 * 轮询得到连接的server
	 * @return
	 */
	public static String pollingConnectServer(){
		long num = POLLING_NUM.incrementAndGet();
		
		try{
			readLock.lock();
			
			int size = connectList.size();
			int i = (int) (num%size);
		
			return connectList.get(i);
		}finally{
			readLock.unlock();
		}
	}

	public static void write(String key, S2sProtocol s2sProtocol) {
		Channel channel = null;
		FixedChannelPool fcp = null;
		try {
			readLock.lock();

			fcp = connectPoolMap.get(key);

			if (fcp == null) {
				log.error("key:{} server is empty", key);
				return;
			}
			
			channel = fcp.acquire().get();
			String json = JSON.toJSONString(s2sProtocol);
			
			channel.writeAndFlush(json);
			
			log.info("key3:{} writeAndFlush:", json);
		}catch(Exception e){
			e.printStackTrace();
			log.error("exception:",e);
			throw new RuntimeException(e);
		}finally {
			if(fcp!=null)
				fcp.release(channel);
			readLock.unlock();
		}
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException{
		ChannelPoolHandler handler = new ChannelPoolHandler() {
			@Override
			public void channelReleased(Channel ch) throws Exception {
		    
			}

			@Override
			public void channelCreated(Channel ch) throws Exception {
	
				ChannelPipeline pipeline = ch.pipeline();
				
				pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
				
				pipeline.addLast(new JsonObjectDecoder());
				pipeline.addLast(new S2sClientInputHandler());
			}

			@Override
			public void channelAcquired(Channel ch) throws Exception {
				
			}
		};

		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		Bootstrap boot = new Bootstrap();
		InetSocketAddress remoteAddress = InetSocketAddress.createUnresolved("127.0.0.1",8888);// 连接地址
		
		
		boot.group(workerGroup)
		.channel(NioSocketChannel.class)
		.option(ChannelOption.TCP_NODELAY, true)
		.option(ChannelOption.SO_KEEPALIVE, true)
		.remoteAddress(remoteAddress);
		
		FixedChannelPool fcp = new FixedChannelPool(boot, handler, Runtime.getRuntime().availableProcessors()*2);
		S2sProtocol s2s = new S2sProtocol();
		s2s.setBody("5555");
		fcp.acquire().get().writeAndFlush(JSON.toJSONString(s2s));
		
		System.out.println("3334455");
	}
	
	/**
	 * 
	 * @param ip
	 * @param port
	 */
	private void putPool(String conServer, String ip, int port) {

		ChannelPoolHandler handler = new ChannelPoolHandler() {
			@Override
			public void channelReleased(Channel ch) throws Exception {
		    
			}

			@Override
			public void channelCreated(Channel ch) throws Exception {
	
				ChannelPipeline pipeline = ch.pipeline();
				
				pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
				
				pipeline.addLast(new JsonObjectDecoder());
				pipeline.addLast(new S2sClientInputHandler());
			}

			@Override
			public void channelAcquired(Channel ch) throws Exception {
				
			}
		};

		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		Bootstrap boot = new Bootstrap();
		InetSocketAddress remoteAddress = InetSocketAddress.createUnresolved(ip,port);// 连接地址
		
        log.info("InetSocketAddress.createUnresolved(ip,port),{},{}",ip,port);
		
		boot.group(workerGroup)
		.channel(NioSocketChannel.class)
		.option(ChannelOption.TCP_NODELAY, true)
		.option(ChannelOption.SO_KEEPALIVE, true)
		.remoteAddress(remoteAddress);
		
		FixedChannelPool fcp = new FixedChannelPool(boot, handler, Runtime.getRuntime().availableProcessors()*2);

		connectPoolMap.put(conServer, fcp);

	}

	@Override
	public void changeNotify(List<ServerDto> serverDtos) {

		try {
			writeLock.lock();

			preDestory();

			for (ServerDto server : serverDtos) {

				String conServer = server.getConnectServer();

				String[] str = conServer.split(ServerDto.SPLIT);
				String ip = str[0];
				int port = Integer.parseInt(str[1]);
				// 初始化连接池
				putPool(conServer, ip, port);

				connectList.add(conServer);
			}
			
			log.info("map pool:{}",connectPoolMap);
			log.info("connectList:{}",connectList);
		} finally {
			writeLock.unlock();
		}

	}

	public String path() {
		return "/easy-im/server";
	}

}
