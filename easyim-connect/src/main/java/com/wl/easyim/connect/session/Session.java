package com.wl.easyim.connect.session;

import com.wl.easyim.biz.api.protocol.c2s.enums.ResourceType;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * 会话对象
 * @author wl
 *
 */
@Data
public class Session {
	//会话状态
	public static enum SessionStatus{
		anonymous,//匿名
		auth;//已登录
	}
	
	private ChannelHandlerContext chc;
	private String userId;
	private String sessionId;
	private long createTime;
	private long updateTime;
	
	//登录状态
	private SessionStatus sessionStatus = SessionStatus.anonymous;
	private ResourceType resource;//设备
	
	private int  timeOutCycle;//超时圈数
	
	private int  timeWheelCurrent;//时间轮的位置
	private int  timeWheelCycle;//进入时间轮的圈数
	
	
}
