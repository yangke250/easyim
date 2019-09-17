package com.easyim.biz.api.dto.protocol;

import java.io.Serializable;
import java.util.UUID;

import com.easyim.biz.api.protocol.enums.s2s.S2sCommandType;

import lombok.Data;

@Data
public class S2sProtocol implements Serializable {
	
	private static final long serialVersionUID = -3602377614974262619L;
	
	private  String uuid = UUID.randomUUID().toString();
	private  String password;

	private  S2sCommandType type;
	private  String body;
	
}
