package com.easyim.biz.api.dto.message;

import java.io.Serializable;

import lombok.Data;

@Data
public class ForwardMsgDto implements Serializable{

	private static final long serialVersionUID = 3662886484033556991L;
	
	private long tenementId;
	private String fromId;
	private String toId;
	private String content;
}
