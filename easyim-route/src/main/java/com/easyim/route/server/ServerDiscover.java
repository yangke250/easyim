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
import com.easyim.route.dto.FixedChannelPoolDto;
import com.easyim.route.inputHandler.S2sClientInputHandler;
import com.easyim.route.inputHandler.pool.RouteChannelPoolHandler;
import com.easyim.route.inputHandler.pool.RouteFixedChannelPool;

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

	private static Map<String, FixedChannelPoolDto> connectPoolMap = new ConcurrentHashMap<String, FixedChannelPoolDto>();

	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static ReadLock readLock = lock.readLock();
	private static WriteLock writeLock = lock.writeLock();

	private static AtomicLong POLLING_NUM = new AtomicLong();

	@PreDestroy
	public void preDestory() {
		for (FixedChannelPoolDto fcp : connectPoolMap.values()) {
			if (fcp != null) {
				fcp.getFixedChannelPool().close();
			}
		}
		
		connectPoolMap.clear();
	}

	
	
	/**
	 * 轮询得到连接的server
	 * @return
	 */
	public static String pollingConnectServer(){
		long num = POLLING_NUM.incrementAndGet();
		
		try{
			readLock.lock();
			
			Set<String> set = connectPoolMap.keySet();
			int size = set.size();
			int i = (int) (num%size);
		
			String[] arrays = (String[]) set.toArray();
			return arrays[i];
		}finally{
			readLock.unlock();
		}
	}

	public static void write(String key, S2sProtocol s2sProtocol) {
		Channel channel = null;
		FixedChannelPoolDto fcp = null;
		try {
			readLock.lock();

			fcp = connectPoolMap.get(key);

			if (fcp == null) {
				log.error("key:{} server is empty", key);
				return;
			}
			
			channel = fcp.getFixedChannelPool().acquire().get();
			
			s2sProtocol.setPassword(fcp.getPassword());
			String json = JSON.toJSONString(s2sProtocol);
			
			channel.writeAndFlush(json);
			
			log.info("key3:{} writeAndFlush:", json);
		}catch(Exception e){
			e.printStackTrace();
			log.error("exception:",e);
			throw new RuntimeException(e);
		}finally {
			if(fcp!=null)
				fcp.getFixedChannelPool().release(channel);
			readLock.unlock();
		}
	}

	
	/**
	 * 
	 * @param ip
	 * @param port
	 */
	private void putPool(String conServer, String ip, int port,String password) {


		EventLoopGroup workerGroup = new NioEventLoopGroup();

		
		RouteChannelPoolHandler handler = new RouteChannelPoolHandler();

		Bootstrap boot = new Bootstrap();
		InetSocketAddress remoteAddress = InetSocketAddress.createUnresolved(ip,port);// 连接地址
		
        log.info("InetSocketAddress.createUnresolved(ip,port),{},{}",ip,port);
		
		boot.group(workerGroup)
		.channel(NioSocketChannel.class)
		.option(ChannelOption.TCP_NODELAY, true)
		.option(ChannelOption.SO_KEEPALIVE, true)
		.remoteAddress(remoteAddress);
		
		RouteFixedChannelPool fcp = new RouteFixedChannelPool(boot, handler, Runtime.getRuntime().availableProcessors()*2);
		
		FixedChannelPoolDto dto = new FixedChannelPoolDto();
		dto.setFixedChannelPool(fcp);
		dto.setPassword(password);
		connectPoolMap.put(conServer, dto);

	}
	

	@Override
	public void changeNotify(List<ServerDto> serverDtos) {

		try {
			writeLock.lock();

			preDestory();
			
			log.info("serverDtos:{}",JSON.toJSONString(serverDtos));

			for (ServerDto server : serverDtos) {

				String conServer = server.getConnectServer();

				String[] str = conServer.split(ServerDto.SPLIT);
				String ip = str[0];
				int port = Integer.parseInt(str[1]);
				// 初始化连接池
				putPool(conServer, ip, port,server.getPassword());

			}
			
			log.info("map pool:{}",connectPoolMap);
		} finally {
			writeLock.unlock();
		}

	}

	public String path() {
		return "/easy-im/server";
	}

}
