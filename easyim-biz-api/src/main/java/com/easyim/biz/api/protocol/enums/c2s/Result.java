package com.easyim.biz.api.protocol.enums.c2s;

public enum Result {

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
