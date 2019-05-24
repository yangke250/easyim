package com.wl.easyim.biz.api.dto.message;


import com.wl.easyim.biz.api.protocol.enums.c2s.Result;

import lombok.Data;

@Data
public class MessageSendResultDto {
	private long id;
	private Result result = Result.success;
}
