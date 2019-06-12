package com.easyim.biz.api.dto.message;

import lombok.Data;

@Data
public class SearchMsgCidDto {
	private long tenementId;
	private long cid;
	private String userId;
	private long minMsgId;
}
