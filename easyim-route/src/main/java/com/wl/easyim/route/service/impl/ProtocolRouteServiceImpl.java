package com.wl.easyim.route.service.impl;

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
import com.wl.easyim.biz.api.dto.protocol.c2s.C2sProtocol;
import com.wl.easyim.biz.api.dto.protocol.s2s.S2sProtocol;
import com.wl.easyim.biz.api.protocol.enums.s2s.S2sCommandType;
import com.wl.easyim.route.server.discover.ServerDiscover;
import com.wl.easyim.route.service.IProtocolRouteService;
import com.wl.easyim.route.service.IUserRouteService;

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
	
	//线程池
	private static ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM);
	
	//同步等待结果
	private static ConcurrentHashMap<String,Queue<C2sProtocol>> inputOutputMap 
	 = new ConcurrentHashMap<String,Queue<C2sProtocol>>();
	
	@Resource
	private IUserRouteService userRouteService;
	
	public static void addCallBack(String protocol){
		C2sProtocol c2sProtocol =  JSON.parseObject(protocol,C2sProtocol.class);
		Queue<C2sProtocol> queue = inputOutputMap.get(c2sProtocol.getUuid());
		if(queue!=null){
			queue.add(c2sProtocol);
		}
	}
	
	
	private S2sProtocol createS2sProtocol(String body){
		S2sProtocol s2sProtocol = new S2sProtocol();
		
		s2sProtocol.setUuid(UUID.randomUUID().toString());
		s2sProtocol.setBody(body);
		s2sProtocol.setType(S2sCommandType.s2sMessagePush);
		
		return s2sProtocol;
	}

	private void route(String routeInfo,String body){
		S2sProtocol  s2sProtocol = createS2sProtocol(body);
		//添加队列
		LinkedBlockingQueue<C2sProtocol> queue = new LinkedBlockingQueue<C2sProtocol>();
		try{
			inputOutputMap.put(s2sProtocol.getUuid(),queue);
			
			ServerDiscover.write(routeInfo, s2sProtocol);
		
			Future<C2sProtocol> future = executorService.submit(new Callable<C2sProtocol>() {
				@Override
				public C2sProtocol call() throws Exception {
						return queue.poll(TIME_OUT, TimeUnit.MILLISECONDS);
				}
			});
		
			C2sProtocol result;
			try {
				result = future.get(TIME_OUT, TimeUnit.MILLISECONDS);
				if(result==null){
					future.cancel(true);
					throw new RuntimeException("handle time out:"+body);
				}
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}finally{
			inputOutputMap.remove(s2sProtocol.getUuid());
		}
	}
	
	@Override
	public boolean route(long tenementId, String userId, String body) {
		String routeInfo = this.userRouteService.getUserRoute(tenementId, userId);
		if(StringUtils.isEmpty(routeInfo)){
			return false;
		}
		
		route(routeInfo,body);
		
		
		return true;
	}

	
	@Override
	public boolean routeAsyn(long tenementId, String userId, String body) {
		String routeInfo = this.userRouteService.getUserRoute(tenementId, userId);
		if(StringUtils.isEmpty(routeInfo)){
			return false;
		}
		
		Observable.create(new ObservableOnSubscribe<S2sProtocol>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<S2sProtocol> e) throws Exception {
            	route(routeInfo,body);
            	e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
		.subscribe(new Observer<S2sProtocol>(){
			@Override
			public void onSubscribe(Disposable d) {
			}

			@Override
			public void onNext(S2sProtocol t) {
			}

			@Override
			public void onError(Throwable e) {
			}

			@Override
			public void onComplete() {
			}

			
		});
		
		
		return true;
	}

}
