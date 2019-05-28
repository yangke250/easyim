package com.wl.easyim.connect.session;

import java.util.UUID;

import com.wl.easyim.biz.api.protocol.enums.c2s.ResourceType;

import io.netty.channel.ChannelHandlerContext;
import lombok.Builder;
import lombok.Data;

/**
 * 会话对象
 * @author wl
 *
 */
@Data
@Builder
public class Session {
	//会话状态
	public static enum SessionStatus{
		anonymous,//匿名
		auth;//已登录
	}
	
	
	private long createTime = System.currentTimeMillis();
	private long updateTime = System.currentTimeMillis();
	
	//登录状态
	private SessionStatus sessionStatus = SessionStatus.anonymous;
	
	private ChannelHandlerContext chc;
	private long tenementId;
	private String userId;
	private ResourceType resource;//设备
	
	private int  timeOutCycle = 1;//超时圈数
	
	
	public String getSessionId(){
		return chc.channel().id().asLongText();
	}
	
	public static String getSessionId(ChannelHandlerContext chc){
		return chc.channel().id().asLongText();
	}
	
	
	
}
