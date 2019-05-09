package com.wl.easyim.biz.api.protocol.c2s.dto;

import lombok.Data;


@Data
public class C2sProtocol{
	private  String body;
	private  String uuid;
	private  CommandType type;
	private  String version;
	
	public static enum CommandType{
		groupMessagePush {
			@Override
			public	CommandType getAckCommand() {
				return groupMessagePushAck;
			}
		},//群组消息推送
		groupMessagePushAck {
			@Override
			public	CommandType getAckCommand() {
				// TODO Auto-generated method stub
				return null;
			}
		},
		readGroupMessagePush {
			@Override
			public	CommandType getAckCommand() {
				return readGroupMessagePushAck;
			}
		},//群组消息推送
		readGroupMessagePushAck {
			@Override
			public	CommandType getAckCommand() {
				// TODO Auto-generated method stub
				return null;
			}
		},
		auth {
			@Override
			public	CommandType getAckCommand() {
				return authAck;
			}
		},//验证身份 建立连接
		authAck {
			@Override
			public	CommandType getAckCommand() {
				// TODO Auto-generated method stub
				return null;
			}
		},//验证结果的返回
		ping {
			@Override
			public	CommandType getAckCommand() {
				// TODO Auto-generated method stub
				return pingAck;
			}
		},//心跳监控
		pingAck {
			@Override
			public	CommandType getAckCommand() {
				// TODO Auto-generated method stub
				return null;
			}
		},//心跳回复
		message {
			@Override
			public	CommandType getAckCommand() {
				return messageAck;
			}
		},//发送消息
		messageAck {
			@Override
			public	CommandType getAckCommand() {
				return null;
			}
		},//消息回复
		messagePush {
			@Override
			public	CommandType getAckCommand() {
				return messagePushAck;
			}
		},//消息推送
		messagePushAck {
			@Override
			public	CommandType getAckCommand() {
				return null;
			}
		},//消息推送ack
		closeSession {
			@Override
			public	CommandType getAckCommand() {
				return closeSessionAck;
			}
		},//请求关闭会话
		closeSessionAck {
			@Override
			public	CommandType getAckCommand() {
				return null;
			}
		},//关闭会话
		readMessagePush {
			@Override
			public	CommandType getAckCommand() {
				return readMessagePushAck;
			}
		},
		readMessagePushAck {
			@Override
			public	CommandType getAckCommand() {
				return null;
			}
		};//未读消息推送
		public abstract CommandType getAckCommand();
	}
	
}
