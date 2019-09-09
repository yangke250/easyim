package com.easyim.biz.api.dto.conversation;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @author wl
 *
 */
@Data
public class ConversationDto implements Serializable{

	private static final long serialVersionUID = -4243417173794697070L;

	private long tenementId;
	private long cid;
	private long proxyCid;
	private String fromId;
	private String toId;
	
}
