package com.wl.easyim.biz.api.protocol.s2s.enums;


public enum S2sCommandType {


	messagePush {
		@Override
		public	S2sCommandType getAckCommand() {
			return messagePushAck;
		}
	},//消息推送
	messagePushAck {
		@Override
		public	S2sCommandType getAckCommand() {
			return null;
		}
	};
	
	public abstract S2sCommandType getAckCommand();

}
