package com.easyim.biz.api.dto.user;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.easyim.biz.api.protocol.enums.c2s.ResourceType;

import lombok.Data;

@Data
public class UserAuthDto {
	@Min(value = 1)
	private long tenementId;
	@NotNull
	private String userId;
	@NotNull
	private ResourceType resourceType;
}
