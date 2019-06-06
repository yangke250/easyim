package com.easyim.biz.api.dto.protocol.c2s;

import java.io.Serializable;
import java.util.UUID;

import com.easyim.biz.api.protocol.enums.c2s.C2sCommandType;

import lombok.Builder;
import lombok.Data;


@Data
public class C2sProtocol implements Serializable{
	
	private static final long serialVersionUID = 9159509431662007255L;
	
	private  String uuid = UUID.randomUUID().toString();
	private  String version = "1.0";

	private  C2sCommandType type;
	private  String body;
	
	public C2sProtocol(){
		
	}
	
	public C2sProtocol(C2sCommandType type){
		this.type = type;
	}
	
	public C2sProtocol(C2sCommandType type,String body){
		this.type = type;
		this.body = body;
	}
}
