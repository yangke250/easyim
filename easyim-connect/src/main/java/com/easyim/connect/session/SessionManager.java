package com.easyim.connect.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.easy.springboot.c2s.server.AbstractServerRegister;
import com.easyim.biz.api.dto.protocol.C2sProtocol;
import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.c2s.AuthAck;
import com.easyim.biz.api.protocol.c2s.CloseSession;
import com.easyim.biz.api.protocol.enums.c2s.C2sCommandType;
import com.easyim.biz.api.protocol.enums.c2s.ResourceType;
import com.easyim.biz.api.protocol.enums.c2s.Result;
import com.easyim.biz.api.service.protocol.IC2sHandleService;
import com.easyim.connect.session.Session.SessionStatus;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * session的会话管理
 * @author wl
 *
 */
@Slf4j
@Component
public class SessionManager {
	
	private static Map<String,ConcurrentHashMap<String,Session>> uidMap 
		=new ConcurrentHashMap<String,ConcurrentHashMap<String,Session>>();
	
	
	private static Map<String,Session> sessionIdMap	
		=new ConcurrentHashMap<String,Session>();

	
	public static final String SPLIT="_";
	
	public static final C2sProtocol TIMEOUT = new C2sProtocol();

	static{
		TIMEOUT.setType(C2sCommandType.closeSession);
		
		CloseSession cs = new CloseSession();
		cs.setResult(Result.timeOut);
		
		TIMEOUT.setBody(JSON.toJSONString(cs));
	}
	
	public static Session getSession(ChannelHandlerContext chc){
		return sessionIdMap.get(Session.getSessionId(chc));
	}

	/**
	 * 得到会话列表
	 * @param uid
	 * @return
	 */
	public static List<Session> getSession(String uid){
		ConcurrentHashMap<String,Session> map =  uidMap.get(uid);
		log.info("getSession:{},{}",uid,uidMap);
		
		
		List<Session> list = new ArrayList<Session>();
		
		if(map!=null){
			list.addAll(map.values());
		}
		
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
		
		Session session = sessionIdMap.get(Session.getSessionId(chc));
		if(session!=null){
			//移除会话
			
			String uid =getUid(session.getTenementId(),session.getUserId());
			
			ConcurrentHashMap<String,Session> sessionMap = uidMap.get(uid);
			if(sessionMap!=null){
				sessionMap.remove(session.getSessionId());
			}
			
			if(sessionMap.size()==0){
				synchronized(uidMap){
					if(sessionMap.size()==0){
						uidMap.remove(uid);
					}
				}
			}
			
			log.info("uid map:{},{}",uid,sessionMap);
		}
		
		//doserver logout
		
		sessionIdMap.remove(Session.getSessionId(chc));
		
		if(c2sProtocol!=null){
			String json  = JSON.toJSONString(c2sProtocol);
			chc.channel().writeAndFlush(json);
		}
		
		chc.close();
	}

	/**
	 * 更新会话状态为已登录
	 * @param session
	 */
	public static boolean addSession(ChannelHandlerContext chc,AuthAck authAck,int timeOutCycle){
		
		Session session = Session
				.builder()
				.chc(chc).sessionStatus(SessionStatus.auth)
				.tenementId(authAck.getTenementId()).userId(authAck.getUserId()).resource(authAck.getResource())
				.timeOutCycle(timeOutCycle)
				.build();
				
		
		String uid =getUid(session.getTenementId(),session.getUserId());
		
		ConcurrentHashMap<String,Session> map = uidMap.get(uid);
		if(map==null){
			synchronized(uidMap){
				map = uidMap.get(uid);
				if(map==null){//double check
					map =  new ConcurrentHashMap<String,Session>();
				}
				uidMap.put(uid,map);
			}
		}
		
		map.put(session.getSessionId(),session);
	

		log.info("uid map:{},{}",uid,map.size());
		
		sessionIdMap.put(Session.getSessionId(chc),session);
		
		return true;
	}
	

	
	
	public static UserSessionDto getUserDto(ChannelHandlerContext chc){
		
		String connectServer = AbstractServerRegister.getConnectServer();
		
		Session session      = sessionIdMap.get(Session.getSessionId(chc));
		
		
		
		UserSessionDto userSessionDto = new UserSessionDto();
		userSessionDto.setConnectServer(connectServer);
		userSessionDto.setSessionId(Session.getSessionId(chc));
		
		if(session!=null){
			userSessionDto.setTenementId(session.getTenementId());
			userSessionDto.setUserId(session.getUserId());
			userSessionDto.setResourceType(session.getResource());
			userSessionDto.setSessionTimeOut(session.getTimeOutCycle()*60);
		}else{
			userSessionDto.setSessionTimeOut(60);
		}
		
		return userSessionDto;
	}
}
