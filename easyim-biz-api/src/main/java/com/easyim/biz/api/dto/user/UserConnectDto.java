package com.easyim.biz.api.dto.user;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户建立连接相关接口
 * @author wl
 *
 */
@Data
public class UserConnectDto implements Serializable{

	private static final long serialVersionUID = -6410588819516052934L;
	
	private String connectServer;//连接的server
	private String token;
	
}
