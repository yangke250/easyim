package com.wl.easyim.biz.api.protocol.c2s.dto;

import java.util.UUID;

import com.wl.easyim.biz.api.protocol.c2s.enums.C2sCommandType;

import lombok.Data;


@Data
public class C2sProtocol{
	private  String uuid = UUID.randomUUID().toString();
	private  String version = "1.0";

	private  C2sCommandType type;
	private  String body;
}
