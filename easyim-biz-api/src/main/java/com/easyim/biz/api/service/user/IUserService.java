package com.easyim.biz.api.service.user;

import java.util.List;


/**
 * 用户在线状态查询
 * @author wl
 *
 */
public interface IUserService {
	/**
	 * 查询用户在线状态
	 * @param tenementId
	 * @param userIds
	 * @return
	 */
	List<String> selectUserOnline(long tenementId,List<String> userIds);
}
