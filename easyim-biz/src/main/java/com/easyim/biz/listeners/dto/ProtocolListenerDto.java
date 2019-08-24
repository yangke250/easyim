package com.easyim.biz.listeners.dto;

import com.easyim.biz.api.dto.protocol.C2sProtocol;
import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.c2s.AbstractProtocol;
import com.easyim.biz.api.protocol.c2s.AbstractResultProtocol;
import com.easyim.biz.api.protocol.enums.c2s.C2sType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolListenerDto {
	private C2sType c2sType;
	private C2sProtocol input;
	private C2sProtocol output;
	private UserSessionDto userSessionDto;
}
