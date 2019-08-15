package com.easyim.route.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
//import com.easy.springboot.redis.template.RedisTemplate;
import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.route.constant.Constant;
import com.easyim.route.service.IUserRouteService;

import cn.linkedcare.springboot.redis.template.RedisTemplate;

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
		long result = redisTemplate.setnx(strKey,JSON.toJSONString(routeDto));
		if(result==0){
			//服务设置不一致的时候
			String  value = redisTemplate.get(strKey);
			UserSessionDto redisSession = JSON.parseObject(value,UserSessionDto.class);
			
			if(!routeDto.getConnectServer().equals(redisSession.getConnectServer())){
				return false;
			}
			
			long ttl = redisTemplate.ttl(strKey);
			if(timeOut>ttl){//如果超时时间大于，已设置的超时时间
				redisTemplate.expire(strKey,timeOut);
			}
			
			//设置同一个用户有多少链接
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

	

	@Override
	public List<String> getOnlineUsers(long tenementId,List<String> userIds){
		List<String> uids = new ArrayList<String>();
		
		for(String userId:userIds){
			uids.add(Constant.getUid(tenementId,userId));
		}
		
		List<String> onlineUsers = new ArrayList<String>();
		List<String> strs = redisTemplate.mget(uids.toArray(new String[]{}));
		for(String str:strs){
			UserSessionDto session = JSON.parseObject(str,UserSessionDto.class);
			onlineUsers.add(session.getUserId());
		}
		
		return onlineUsers;
	}

}
