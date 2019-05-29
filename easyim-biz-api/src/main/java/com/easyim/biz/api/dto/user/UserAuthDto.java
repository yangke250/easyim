package com.wl.easyim.biz.api.dto.user;

import com.wl.easyim.biz.api.protocol.enums.c2s.ResourceType;

import lombok.Data;

@Data
public class UserAuthDto {
	private long tenementId;
	private String userId;
	private ResourceType resourceType;
}
