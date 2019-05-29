package com.easyim.biz.api.dto.message;


import com.easyim.biz.api.protocol.enums.c2s.Result;

import lombok.Data;

@Data
public class SendMsgResultDto {
	private long id;
	private Result result = Result.success;
}
