package com.easyim.biz.api.dto.message;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.easyim.biz.api.protocol.enums.c2s.ResourceType;

import lombok.Data;


/**
 * 查询未同步消息
 * @author wl
 *
 */
@Data
public class OfflineMsgDto {
	@Min(value=1)
	private long tenementId;
	@NotBlank
	private String userId;
	@Min(value=0)
	private long lastMsgId = 0;
}
