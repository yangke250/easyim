package com.wl.easyim.biz.api.protocol.c2s.protocol;

import com.wl.easyim.biz.api.protocol.c2s.enums.Result;

import lombok.Data;

@Data
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
	
	
}
