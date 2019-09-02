package com.easyim.route.service;

import java.util.List;

import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.enums.c2s.ResourceType;

/**
 * 用户路由服务
 * @author wl
 *
 */
public interface IUserRouteService {
	

	/**
	 * 得到在线的用户列表
	 * @param tenementId
	 * @param userIds
	 * @return
	 */
	public List<String> getOnlineUsers(long tenementId,List<String> userIds);
	
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
	public UserSessionDto getUserRoute(long tenementId,String userId);
	
	/**
	 * 用户增加路由信息
	 * @param routeDto
	 * @return
	 */
	public boolean addUserRoute(UserSessionDto routeDto);
	
	/**
	 * 重置超时时间
	 * @param routeDto
	 * @return
	 */
	public boolean pingUserRoute(UserSessionDto routeDto);
	
	/**
	 * 删除路由数据
	 * @param tenementId
	 * @param userId
	 * @param msg
	 */
	public boolean removeUserRoute(UserSessionDto routeDto);

}
