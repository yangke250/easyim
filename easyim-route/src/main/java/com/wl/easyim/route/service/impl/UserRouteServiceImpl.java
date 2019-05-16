package com.wl.easyim.route.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wl.easyim.route.dto.RouteDto;
import com.wl.easyim.route.service.IUserRouteService;

import cn.linkedcare.springboot.redis.template.RedisTemplate;

@Service
public class UserRouteServiceImpl implements IUserRouteService {

	@Resource
	private RedisTemplate redisTemplate;
	
	private final static String ROUTE_STRING_PRE ="easyim_rote_s";
	
	private final static String ROUTE_HASH_PRE   ="easyim_rote_h";
	
	private final static String SPLIT="_";
	
	public boolean addUserRoute(RouteDto routeDto, int timeOut) {
		String uid       =  routeDto.getTenementId()+SPLIT+routeDto.getUserId();
		String sessionId =  routeDto.getSessionId();
		String strKey  =  ROUTE_STRING_PRE+uid;
		String hashKey =  ROUTE_HASH_PRE+uid;
		
		
		long result = redisTemplate.setnx(strKey,routeDto.getConnectServer());
		if(result==0){
			String  value = redisTemplate.get(strKey);
			if(!routeDto.getConnectServer().equals(value)){
				return false;
			}
			
			long ttl = redisTemplate.ttl(strKey);
			if(timeOut>ttl){
				redisTemplate.expire(strKey,timeOut);
			}
			
			redisTemplate.hset(hashKey,sessionId,sessionId);
			ttl = redisTemplate.ttl(hashKey);
			if(timeOut>ttl){
				redisTemplate.expire(hashKey,timeOut);
			}
		}else{
			redisTemplate.expire(strKey,timeOut);
			redisTemplate.hset(hashKey,sessionId,sessionId);
			redisTemplate.expire(hashKey,timeOut);
		}
		
		return true;
	}

	public boolean pingUserRoute(RouteDto routeDto, int timeOut) {
		String uid       =  routeDto.getTenementId()+SPLIT+routeDto.getUserId();
		String strKey  =  ROUTE_STRING_PRE+uid;
		String hashKey =  ROUTE_HASH_PRE+uid;
		
		long ttl = redisTemplate.ttl(strKey);
		if(timeOut>ttl){
			long result = redisTemplate.expire(strKey,timeOut);
			if(result<=0){
				return false;
			}
		}
		
		ttl =  redisTemplate.ttl(hashKey);
		if(timeOut>ttl){
			long result = redisTemplate.expire(hashKey,timeOut);
			if(result<=0){
				return false;
			}
		}
		return true;
	}


	public boolean removeUserRoute(RouteDto routeDto, int timeOut) {
		String uid       =  routeDto.getTenementId()+SPLIT+routeDto.getUserId();
		String sessionId =  routeDto.getSessionId();
		String strKey  =  ROUTE_STRING_PRE+uid;
		String hashKey =  ROUTE_HASH_PRE+uid;
		
		redisTemplate.hdel(hashKey,sessionId);
		
		long length = redisTemplate.hlen(hashKey);
		if(length==0){
			redisTemplate.del(strKey);
		}
		
		return true;
	}

	

}
