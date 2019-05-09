package com.wl.easyim.biz.api.protocol.c2s.protocol;


public abstract class AbstractAckProtocol extends AbstractProtocol{
	private Result result = Result.success;
	private int code      = Result.success.getCode();
	
	
	
	private String msg = null;
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
		this.msg  = result.getMsg();
		this.code = result.getCode();
	}
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
