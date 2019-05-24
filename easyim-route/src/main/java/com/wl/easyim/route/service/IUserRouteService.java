package com.wl.easyim.route.service;

import com.wl.easyim.biz.api.dto.protocol.s2s.UserDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.ResourceType;

/**
 * 用户路由服务
 * @author wl
 *
 */
public interface IUserRouteService {
	
	/**
	 * 用户是否在线
	 * @param tenementId
	 * @param userId
	 * @return
	 */
	public boolean isOnline(long tenementId,String userId);
	
	/**
	 * 得到用户所在的服务器
	 * @param tenementId
	 * @param userId
	 * @return
	 */
	public String getUserRoute(long tenementId,String userId);
	
	/**
	 * 用户增加路由信息
	 * @param routeDto
	 * @return
	 */
	public boolean addUserRoute(UserDto routeDto);
	
	/**
	 * 重置超时时间
	 * @param routeDto
	 * @return
	 */
	public boolean pingUserRoute(UserDto routeDto);
	
	/**
	 * 删除路由数据
	 * @param tenementId
	 * @param userId
	 * @param msg
	 */
	public boolean removeUserRoute(UserDto routeDto);

}
