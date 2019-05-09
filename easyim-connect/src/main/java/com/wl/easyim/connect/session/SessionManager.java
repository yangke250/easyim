package com.wl.easyim.connect.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.wl.easyim.biz.api.protocol.c2s.dto.C2sProtocol;
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


	
	/**
	 * 删除会话
	 * @param session
	 */
	public static void removeSession(ChannelHandlerContext chc,C2sProtocol c2sProtocol){
		String json  = JSON.toJSONString(c2sProtocol);
		chc.writeAndFlush(json);
		
		Session session = sessionMap.get(chc);
		if(session==null){
			return;
		}
		
		//doserver logout
		
		//移除会话
		String userId = session.getUserId();
		uidMap.get(userId).remove(session);
		sessionMap.remove(chc);
		
		SessionTimeWheel.removeTimeWheel(session);
	}

	/**
	 * 更新会话状态为已登录
	 * @param session
	 */
	public static void updateSessionStatus(ChannelHandlerContext chc){
		Session session = sessionMap.get(chc);
		
		session.setSessionStatus(SessionStatus.auth);
	}
	
	/**
	 * 添加session
	 * @param session
	 */
	public static void addSession(Session session){
		String uid  = session.getUserId();
		ConcurrentHashMap<Session,Session> map = uidMap.get(uid);
		if(map==null){
			synchronized(uidMap){
				map = uidMap.get(uid);
				if(session==null){//double check
					map =  new ConcurrentHashMap<Session,Session>();
					map.put(session,session);
					
					uidMap.put(uid,map);
				}
			}
		}
		
		
		sessionMap.put(session.getChc(),session);
	}
	
}
