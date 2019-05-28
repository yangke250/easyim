package com.wl.easyim.route.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.wl.easy.springboot.redis.template.RedisTemplate;
import com.wl.easyim.biz.api.dto.user.UserSessionDto;
import com.wl.easyim.route.constant.Constant;
import com.wl.easyim.route.service.IUserRouteService;

@Service
public class UserRouteServiceImpl implements IUserRouteService {

	@Resource
	private RedisTemplate redisTemplate;
	
	
	
	/**
	 * 添加用户路由信息
	 */
	@Override
	public boolean addUserRoute(UserSessionDto routeDto) {
		String uid       =  Constant.getUid(routeDto.getTenementId(),routeDto.getUserId());
		String sessionId =  routeDto.getSessionId();
		String strKey  =  Constant.getRouteString(uid);
		String hashKey =  Constant.getRouteHash(uid);
		
		int timeOut = routeDto.getSessionTimeOut();
		
		if(StringUtils.isEmpty(uid)
				||StringUtils.isEmpty(sessionId)||timeOut<=0){
			return false;
		}
		
		
		//设置用户路由地址
		long result = redisTemplate.setnx(strKey,routeDto.getConnectServer());
		if(result==0){
			//服务设置不一致的时候
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

	/**
	 * 更新用户路由超时时间
	 */
	@Override
	public boolean pingUserRoute(UserSessionDto routeDto) {
		String uid       =  Constant.getUid(routeDto.getTenementId(),routeDto.getUserId());
		String sessionId =  routeDto.getSessionId();
		String strKey  =  Constant.getRouteString(uid);
		String hashKey =  Constant.getRouteHash(uid);
		
		int timeOut = routeDto.getSessionTimeOut();
		
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

	/**
	 * 删除用户路由信息
	 */
	@Override
	public boolean removeUserRoute(UserSessionDto routeDto) {
		String uid       =  Constant.getUid(routeDto.getTenementId(),routeDto.getUserId());
		String sessionId =  routeDto.getSessionId();
		String strKey  =  Constant.getRouteString(uid);
		String hashKey =  Constant.getRouteHash(uid);
		
		redisTemplate.hdel(hashKey,sessionId);
		
		long length = redisTemplate.hlen(hashKey);
		if(length==0){
			redisTemplate.del(strKey);
		}
		
		return true;
	}

	@Override
	public String getUserRoute(long tenementId, String userId) {
		String uid       =  Constant.getUid(tenementId,userId);
		String strKey  =  Constant.getRouteString(uid);
		
		return redisTemplate.get(strKey);
	}

	@Override
	public boolean isOnline(long tenementId, String userId) {
		String route = getUserRoute(tenementId,userId);
		if(StringUtils.isEmpty(route)){
			return false;
		}
		return true;
	}

	

}
