package com.wl.easyim.biz.api.protocol.c2s.enums;


public enum C2sCommandType {

	groupMessagePush {
		@Override
		public	C2sCommandType getAckCommand() {
			return groupMessagePushAck;
		}
	},//群组消息推送
	groupMessagePushAck {
		@Override
		public	C2sCommandType getAckCommand() {
			// TODO Auto-generated method stub
			return null;
		}
	},
	readGroupMessagePush {
		@Override
		public	C2sCommandType getAckCommand() {
			return readGroupMessagePushAck;
		}
	},//群组消息推送
	readGroupMessagePushAck {
		@Override
		public	C2sCommandType getAckCommand() {
			// TODO Auto-generated method stub
			return null;
		}
	},
	auth {
		@Override
		public	C2sCommandType getAckCommand() {
			return authAck;
		}
	},//验证身份 建立连接
	authAck {
		@Override
		public	C2sCommandType getAckCommand() {
			// TODO Auto-generated method stub
			return null;
		}
	},//验证结果的返回
	ping {
		@Override
		public	C2sCommandType getAckCommand() {
			// TODO Auto-generated method stub
			return pingAck;
		}
	},//心跳监控
	pingAck {
		@Override
		public	C2sCommandType getAckCommand() {
			// TODO Auto-generated method stub
			return null;
		}
	},//心跳回复
	message {
		@Override
		public	C2sCommandType getAckCommand() {
			return messageAck;
		}
	},//发送消息
	messageAck {
		@Override
		public	C2sCommandType getAckCommand() {
			return null;
		}
	},//消息回复
	messagePush {
		@Override
		public	C2sCommandType getAckCommand() {
			return messagePushAck;
		}
	},//消息推送
	messagePushAck {
		@Override
		public	C2sCommandType getAckCommand() {
			return null;
		}
	},//消息推送ack
	closeSession {
		@Override
		public	C2sCommandType getAckCommand() {
			return closeSessionAck;
		}
	},//请求关闭会话
	closeSessionAck {
		@Override
		public	C2sCommandType getAckCommand() {
			return null;
		}
	},//关闭会话
	readMessagePush {
		@Override
		public	C2sCommandType getAckCommand() {
			return readMessagePushAck;
		}
	},
	readMessagePushAck {
		@Override
		public	C2sCommandType getAckCommand() {
			return null;
		}
	},//kick off
	kickOff {
		@Override
		public	C2sCommandType getAckCommand() {
			return C2sCommandType.kickOffAck;
		}
	},
	kickOffAck {
		@Override
		public	C2sCommandType getAckCommand() {
			return null;
		}
	}
	;//未读消息推送
	public abstract C2sCommandType getAckCommand();

}
