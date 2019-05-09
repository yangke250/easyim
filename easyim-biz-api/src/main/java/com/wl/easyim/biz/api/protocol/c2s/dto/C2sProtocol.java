package com.wl.easyim.biz.api.protocol.c2s.dto;

import java.util.UUID;

import com.wl.easyim.biz.api.protocol.c2s.enums.CommandType;

import lombok.Data;


@Data
public class C2sProtocol{
	private  String uuid = UUID.randomUUID().toString();
	private  String version;

	private  CommandType type;
	private  String body;
}
