package com.wl.easyim.biz.server.discover;

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
import com.wl.easyim.biz.api.protocol.s2s.dto.S2sProtocol;

import cn.linkedcare.springboot.sr2f.client.ISr2fClient;
import cn.linkedcare.springboot.sr2f.dto.ServerDto;
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

@Component
@Slf4j
public class ServerDiscover implements ISr2fClient {

	private final static long TIME_OUT  = 2000l;//写入2秒没有应答，代表处理失败
	
	private final static int THREAD_NUM = 100;

	private final static String SPLIT =":";
	
	private static Map<String,FixedChannelPool> poolMap = new ConcurrentHashMap<String,FixedChannelPool>();
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM);
	
	private static ConcurrentHashMap<String,Queue<String>> inputOutputMap
	= new ConcurrentHashMap<String,Queue<String>>();
	
	@PreDestroy
	public void preDestory(){
		for(FixedChannelPool fcp:poolMap.values()){
			if(fcp!=null){
				fcp.close();
			}
		}
	}
	
	/**
	 * 协议回写
	 * @param uuid
	 * @param s2sProtocol
	 */
	public static void writeCallback(String uuid,S2sProtocol s2sProtocol){
		Queue<String> queue = inputOutputMap.get(uuid);
		if(queue!=null){
			queue.add(JSON.toJSONString(s2sProtocol));
		}
	}
	
	public static void write(String key,String json){
		FixedChannelPool fcp = poolMap.get(key);
		
		if(fcp==null){
			log.warn("key:{} server is empty",key);
			return;
		}
		
		Future<String> future = executorService.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
				Future<Channel> channel = null;
				try{
					channel =  fcp.acquire();
					channel.get().writeAndFlush(json);
					
					LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
					inputOutputMap.put(key, queue);
					
					return queue.poll(TIME_OUT, TimeUnit.MILLISECONDS);
				}finally{
					if(channel!=null){
						fcp.release(channel.get());
					}
				}
				
			}
		});
		
		try {
			String result = future.get(TIME_OUT, TimeUnit.MILLISECONDS);
			if(result==null){
				future.cancel(true);
				throw new RuntimeException("handle time out:"+JSON.toJSONString(json));
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			log.error("exception:",e);
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args){
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		Bootstrap boot = new Bootstrap();
		boot.group(workerGroup).option(ChannelOption.SO_KEEPALIVE, true).channel(NioServerSocketChannel.class)
				.handler(new ChannelInitializer<Channel>() {

					@Override
					protected void initChannel(Channel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new JsonObjectDecoder());
						pipeline.addLast(new S2sBizInputHandler());
					}
				});
		
			boot.connect("127.0.0.1",8888);
	
	}

	/**
	 * 
	 * @param ip
	 * @param port
	 */
	private void putPool(String ip,int port){
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
		
		this.poolMap.put(ip+SPLIT+port,fcp);
	}
	
	public void changeNotify(List<ServerDto> serverDtos) {
		Map<String, Bootstrap> map = new ConcurrentHashMap<String, Bootstrap>();
		
		for (ServerDto server : serverDtos) {

			this.putPool(server.getIp(),server.getPort());
		}
	}

	public String path() {
		return "/easy-im/server";
	}

}
