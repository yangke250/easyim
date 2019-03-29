package com.wl.easyim.biz.api.protocol.c2s.dto;

import lombok.Data;


public abstract interface BaseC2s {
	public enum C2sType{
		auth,//验证
		authAck,//验证ack
		ping,//心跳
		pingAck,//心跳ack
		msg,//单聊消息
		msgAck,//单聊消息ack
		gMsg,//群聊消息
		gMsgAck,//群聊消息ack
		msgPush,//消息推送
		msgPushAck,//消息推送ack
		oMsgPull,//拉取离线消息
		oMsgPullAck;//拉取离线ack
	}
	
	public C2sType type();
}
