package com.easyim.biz.api.protocol.enums.c2s;


public enum EasyImC2sType implements C2sType{
	readMessage {
		@Override
		public C2sType getAck() {
			return groupMessageAck;
		}
	},//发送消息
	readMessageAck {
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	},
	groupMessage {
		@Override
		public	EasyImC2sType getAck() {
			return groupMessageAck;
		}
	},//发送消息
	groupMessageAck {
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	},
	groupMessagePush {
		@Override
		public	EasyImC2sType getAck() {
			return groupMessagePushAck;
		}
	},//群组消息推送
	groupMessagePushAck {
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	},
	readGroupMessagePush {
		@Override
		public	EasyImC2sType getAck() {
			return readGroupMessagePushAck;
		}
	},//群组消息推送
	readGroupMessagePushAck {
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	},
	auth {
		@Override
		public	EasyImC2sType getAck() {
			return authAck;
		}
	},//验证身份 建立连接
	authAck {
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	},//验证结果的返回
	ping {
		@Override
		public	EasyImC2sType getAck() {
			return pingAck;
		}
	},//心跳监控
	pingAck {
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	},//心跳回复
	message {
		@Override
		public	EasyImC2sType getAck() {
			return messageAck;
		}
	},//发送消息
	messageAck {
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	},//消息回复
	messagePush {
		@Override
		public	EasyImC2sType getAck() {
			return messagePushAck;
		}
	},//消息推送
	messagePushAck {
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	},//消息推送ack
	closeSession {
		@Override
		public	EasyImC2sType getAck() {
			return closeSessionAck;
		}
	},//请求关闭会话
	closeSessionAck {
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	},//关闭会话
	readMessagePush {
		@Override
		public	EasyImC2sType getAck() {
			return readMessagePushAck;
		}
	},
	readMessagePushAck {
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	},//kick off
	kickOff {
		@Override
		public	EasyImC2sType getAck() {
			return kickOffAck;
		}
	},
	kickOffAck {
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	},login{
		@Override
		public	EasyImC2sType getAck() {
			return loginAck;
		}
	},loginAck{
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	},logout{
		@Override
		public	EasyImC2sType getAck() {
			return loginOutAck;
		}
	},loginOutAck{
		@Override
		public	EasyImC2sType getAck() {
			return null;
		}
	}

}
