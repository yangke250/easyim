package com.wl.easyim.connect.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.wl.easyim.biz.api.protocol.c2s.dto.C2sProtocol;
import com.wl.easyim.biz.api.protocol.c2s.enums.CommandType;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionTimeWheel {
	private static List<ConcurrentHashMap<Session,Session>> sessionList 
		=new ArrayList<ConcurrentHashMap<Session,Session>>();
	private static volatile int current =0;
	
	
	static{
		for(int i=0;i<60;i++){
			sessionList.add(new ConcurrentHashMap<Session,Session>());
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					while(true){
						ConcurrentHashMap<Session,Session> maps = sessionList.get(current);
						for(Session session:maps.values()){
							synchronized(session){//session并发处理
								if(maps.get(session)==null){
									continue;
								}
								
								ChannelHandlerContext chc = session.getChc();
								
								if(session.getTimeWheelCycle()<=0){
									C2sProtocol c2sProtocol = new C2sProtocol();
									c2sProtocol.setType(CommandType.closeSession);
									
									SessionManager.removeSession(chc, c2sProtocol);
								}else{//圈数减一
								    session.setTimeWheelCurrent(session.getTimeWheelCycle()-1);
								}
							}
						}
						
						current++;
						if(current>=60){
							current =0;
						}
					}
				}catch(Exception e){
					log.info("exception:",e);
				}
			}
		}).start();
		
		
	}
	
	/**
	 * 加入时间轮
	 */
	public static void addTimeWheel(Session session){
		int now = SessionTimeWheel.current;
		if(now==0){
			now =60;
		}else{
			now--;
		}
		
		sessionList.get(now).put(session,session);
	}
	

	/**
	 * 删除时间轮
	 * @param session
	 */
	public static void removeTimeWheel(Session session){
		synchronized(session){
			int current = session.getTimeWheelCurrent();
			
			sessionList.get(current).remove(session);
		}
	}
	
	/**
	 * 重置时间轮
	 * @param session
	 */
	public static void resetTimeWheel(Session session){
		synchronized(session){
			int current = session.getTimeWheelCurrent();
			
			sessionList.get(current).remove(session);
			
			int now  = SessionTimeWheel.current;
			session.setTimeWheelCurrent(now);
			session.setTimeWheelCycle(session.getTimeOutCycle());
			
			sessionList.get(now).put(session,session);
		}
	}
	
}
