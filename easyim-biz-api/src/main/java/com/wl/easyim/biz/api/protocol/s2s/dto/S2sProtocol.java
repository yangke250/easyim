package com.wl.easyim.biz.api.protocol.s2s.dto;

import java.io.Serializable;
import java.util.UUID;

import com.wl.easyim.biz.api.protocol.s2s.enums.S2sCommandType;

import lombok.Data;

@Data
public class S2sProtocol implements Serializable {
	
	private static final long serialVersionUID = -3602377614974262619L;
	
	private  String uuid = UUID.randomUUID().toString();
	private  String version;

	private  S2sCommandType type;
	private  String body;
}
