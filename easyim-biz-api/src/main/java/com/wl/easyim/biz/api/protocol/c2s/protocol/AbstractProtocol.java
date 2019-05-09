package com.wl.easyim.biz.api.protocol.c2s.protocol;

import java.io.Serializable;

/**
 * 所有协议的抽象接口
 * @author wl
 *
 */
public abstract class AbstractProtocol implements Serializable {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = -5647670647931779382L;

	public static enum Result{
		success(200,"成功"),//成功
		inputError(401,"参数错误"),//参数错误
		tokenError(402,"token错误"),//token错误
		authFailed(500,"身份验证失败"),//身份验证失败
		timeOut(501,"心跳超时"),//心跳超时
		kickOff(502,"同一设备登录被踢下线"),//同一设备登录被踢下线
		notAuth(503,"权限错误"),
		sendMsgError(504,"无法发送消息"),//发送微信端消息失败
		serverError(505,"服务端异常"),//服务端异常
		pingError(507,"心跳异常"),//心跳超时
		groupManagerError(508,"非群管理员异常"),
		groupUserNumError(509,"群成员超过上限异常")//心跳超时
		;//
		Result(int code,String msg){
			this.code =code;
		}
		private int code;
		private String msg;
		
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		
	}
	
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