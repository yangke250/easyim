package com.wl.easyim.biz.domain;

import lombok.Data;

/**
 * 租户表
 * @author wl
 *
 */
@Data
public class TenementDo {
	private long id;
	private String name;//描述
	private boolean isMultiDevice;//是否多端登录
	private boolean isMultiConn;//同一端是否允许多个连接
}
