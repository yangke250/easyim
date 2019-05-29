package com.easyim.biz.api.protocol.enums.s2s;


public enum S2sCommandType {


	s2sMessagePush {
		@Override
		public	S2sCommandType getAckCommand() {
			return s2sMessagePushAck;
		}
	},//消息推送
	s2sMessagePushAck {
		@Override
		public	S2sCommandType getAckCommand() {
			return null;
		}
	};
	
	public abstract S2sCommandType getAckCommand();

}
