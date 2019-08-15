package com.easyim.biz.api.dto.user;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserOnlineDto implements Serializable{

	private static final long serialVersionUID = 2557137324151736655L;
	private long tenementId;
	private String userId;
	private boolean online;
}
