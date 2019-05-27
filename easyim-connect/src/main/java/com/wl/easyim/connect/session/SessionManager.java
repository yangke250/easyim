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
import com.wl.easy.springboot.c2s.server.AbstractServerRegister;
import com.wl.easyim.biz.api.dto.protocol.c2s.C2sProtocol;
import com.wl.easyim.biz.api.dto.user.UserSessionDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.ResourceType;
import com.wl.easyim.biz.api.protocol.protocol.c2s.AuthAck;
import com.wl.easyim.biz.api.service.protocol.IC2sHandleService;
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

	
	public static final String SPLIT="_";
	
	
	public static Session getSession(ChannelHandlerContext chc){
		return sessionMap.get(chc);
	}

	/**
	 * 得到会话列表
	 * @param uid
	 * @return
	 */
	public static List<Session> getSession(String uid){
		ConcurrentHashMap<Session,Session> map =  uidMap.get(uid);
		
		List<Session> list = new ArrayList<Session>();
		list.addAll(map.values());
		
		return list;
	}
	
	public static String getUid(long tenementId,String userId){
		return tenementId+SPLIT+userId;
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
			
			String uid =getUid(session.getTenementId(),session.getUserId());
			
			uidMap.get(uid).remove(session);
		
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
		
		Session session = sessionMap.get(chc);
		if(session==null){
			return false;
		}
		
		session.setSessionStatus(SessionStatus.auth);
		session.setUserId(authAck.getUserId());
		session.setResource(authAck.getResource());
		session.setTenementId(authAck.getTenementId());
		
		String uid =getUid(session.getTenementId(),session.getUserId());
		
		ConcurrentHashMap<Session,Session> map = uidMap.get(uid);
		if(map==null){
			synchronized(uidMap){
				map = uidMap.get(uid);
				if(map==null){//double check
					map =  new ConcurrentHashMap<Session,Session>();
				}
					map.put(session,session);
					uidMap.put(uid,map);
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
	
	
	public static UserSessionDto getUserDto(ChannelHandlerContext chc){
		
		String connectServer = AbstractServerRegister.getConnectServer();
		
		Session session      = sessionMap.get(chc);
		
		UserSessionDto userSessionDto = new UserSessionDto();
		userSessionDto.setConnectServer(connectServer);
		
		userSessionDto.setSessionId(session.getSessionId());
		
		userSessionDto.setTenementId(session.getTenementId());
		userSessionDto.setUserId(session.getUserId());
		userSessionDto.setResourceType(session.getResource());
		
		userSessionDto.setSessionTimeOut(session.getTimeOutCycle()*60);
		
		return userSessionDto;
	}
}
