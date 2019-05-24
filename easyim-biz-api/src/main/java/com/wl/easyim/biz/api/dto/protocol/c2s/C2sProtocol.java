package com.wl.easyim.biz.api.dto.protocol.c2s;

import java.util.UUID;

import com.wl.easyim.biz.api.protocol.enums.c2s.C2sCommandType;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class C2sProtocol{
	private  String uuid = UUID.randomUUID().toString();
	private  String version = "1.0";

	private  C2sCommandType type;
	private  String body;
}
