package com.easyim.biz.api.dto.conversation;

import java.io.Serializable;

import lombok.Data;

@Data
public class UnreadDto implements Serializable{
	private long cid;
	private int  unreads;
}
