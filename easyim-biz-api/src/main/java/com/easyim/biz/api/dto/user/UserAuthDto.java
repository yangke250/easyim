package com.easyim.biz.api.dto.user;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.easyim.biz.api.protocol.enums.c2s.ResourceType;

import lombok.Data;

@Data
public class UserAuthDto implements Serializable {
	
	private static final long serialVersionUID = 3937511602862373876L;
	
	@Min(value = 1)
	private long tenementId;
	@NotNull
	private String userId;
	@NotNull
	private ResourceType resourceType;
	//扩展的json
	private String extendJson;
}
