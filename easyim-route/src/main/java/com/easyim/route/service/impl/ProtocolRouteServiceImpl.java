package com.easyim.route.service.impl;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.easyim.biz.api.dto.protocol.C2sProtocol;
import com.easyim.biz.api.dto.protocol.S2sProtocol;
import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.enums.s2s.S2sCommandType;
import com.easyim.biz.api.protocol.s2s.S2sMessagePush;
import com.easyim.route.server.ServerDiscover;
import com.easyim.route.service.IProtocolRouteService;
import com.easyim.route.service.IUserRouteService;

import io.netty.channel.Channel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.IoScheduler;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ProtocolRouteServiceImpl implements IProtocolRouteService{
	
	private final static int THREAD_NUM = 100;
	
	private final static long TIME_OUT  = 2000l;//写入2秒没有应答，代表处理失败

	private ExecutorService eService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	@Resource
	private IUserRouteService userRouteService;
	
	
	
	
	private S2sProtocol createS2sProtocol(String body){
		S2sProtocol s2sProtocol = new S2sProtocol();
		
		s2sProtocol.setUuid(UUID.randomUUID().toString());
		s2sProtocol.setBody(body);
		s2sProtocol.setType(S2sCommandType.s2sMessagePush);
		
		return s2sProtocol;
	}

	private void route(String routeInfo,String body){
		S2sProtocol  s2sProtocol = createS2sProtocol(body);
		//添加消息响应的队列
		ServerDiscover.write(routeInfo, s2sProtocol);
	}
	
	/**
	 * 得到路由的server
	 * @param tenementId
	 * @param userId
	 * @return
	 */
	private String getRouteServer(long tenementId, String userId){
		String server = this.userRouteService.getUserRoute(tenementId, userId);
		if(server==null){
			return null;
		}
		log.info("route info:{},{}",userId,server);
		
		return server;
	}
	
	@Override
	public boolean route(long tenementId, String userId, String body,String excludeSessionId) {
		String routeInfo = getRouteServer(tenementId,userId);
		if(StringUtils.isEmpty(routeInfo)){
			return false;
		}
		
		S2sMessagePush s = new S2sMessagePush();
		s.setTenementId(tenementId);
		s.setToId(userId);
		s.setBody(body);
		s.setExcludeSessionId(excludeSessionId);
		
		route(routeInfo,JSON.toJSONString(s));
		return true;
	}

	
	@Override
	public boolean routeAsyn(long tenementId, String userId, String body,String excludeSessionId) {
		eService.execute(()->{
        	route(tenementId,userId,body,excludeSessionId);
		});
		return true;
	}

}
