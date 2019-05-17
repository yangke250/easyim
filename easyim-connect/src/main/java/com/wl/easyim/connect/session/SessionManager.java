package com.wl.easyim.connect.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.wl.easyim.biz.api.protocol.c2s.dto.C2sProtocol;
import com.wl.easyim.biz.api.protocol.c2s.enums.ResourceType;
import com.wl.easyim.biz.api.protocol.c2s.protocol.AuthAck;
import com.wl.easyim.biz.api.protocol.s2s.dto.UserDto;
import com.wl.easyim.biz.api.protocol.service.IC2sHandleService;
import com.wl.easyim.connect.session.Session.SessionStatus;

import io.netty.channel.ChannelHandlerContext;

/**
 * session的会话管理
 * @author wl
 *
 */
@Component
public class SessionManager {
	
	private static Map<String,ConcurrentHashMap<Session,Session>> uidMap 
		=new ConcurrentHashMap<String,ConcurrentHashMap<Session,Session>>();
	
	
	private static Map<ChannelHandlerContext,Session> sessionMap	
		=new ConcurrentHashMap<ChannelHandlerContext,Session>();

	
	public static Session getSession(ChannelHandlerContext chc){
		return sessionMap.get(chc);
	}

	
	/**
	 * 删除会话
	 * @param session
	 */
	public static void removeSession(ChannelHandlerContext chc,C2sProtocol c2sProtocol){
		String json  = JSON.toJSONString(c2sProtocol);
		chc.writeAndFlush(json);
		
		Session session = sessionMap.get(chc);
		if(session!=null){
			//移除会话
			String userId = session.getUserId();
			uidMap.get(userId).remove(session);
		
			//移除时间轮
			SessionTimeWheel.removeTimeWheel(session);
		}
		
		//doserver logout
		
		sessionMap.remove(chc);
		
	}

	/**
	 * 更新会话状态为已登录
	 * @param session
	 */
	public static boolean updateSessionStatus(ChannelHandlerContext chc,AuthAck authAck,int timeOutCycle){
		String       userId     = authAck.getUserId();
		ResourceType resource   = authAck.getResource();
		long         tenementId = authAck.getTenementId();
		
		Session session = sessionMap.get(chc);
		if(session==null){
			return false;
		}
		
		session.setSessionStatus(SessionStatus.auth);
		session.setUserId(userId);
		session.setResource(resource);
		session.setTenementId(tenementId);
		
		ConcurrentHashMap<Session,Session> map = uidMap.get(userId);
		if(map==null){
			synchronized(uidMap){
				map = uidMap.get(userId);
				if(map==null){//double check
					map =  new ConcurrentHashMap<Session,Session>();
				}
					map.put(session,session);
					uidMap.put(userId,map);
			}
		}
		
		session.setTimeOutCycle(timeOutCycle);
		SessionTimeWheel.resetTimeWheel(session);
		return true;
	}
	
	/**
	 * 添加session
	 * @param session
	 */
	public static void addSession(Session session){
		
		sessionMap.put(session.getChc(),session);
	
		SessionTimeWheel.addTimeWheel(session);
	}
	
	
	public static UserDto getUserDto(ChannelHandlerContext chc){
		UserDto userDto = new UserDto();
		userDto.setConnectServer(connectServer);
		
		
		return userDto;
	}
}
