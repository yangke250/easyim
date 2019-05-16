package com.wl.easyim.route.service;

import com.wl.easyim.biz.api.protocol.c2s.enums.ResourceType;
import com.wl.easyim.route.dto.RouteDto;

/**
 * 用户路由服务
 * @author wl
 *
 */
public interface IUserRouteService {
	/**
	 * 用户增加路由信息
	 * @param routeDto
	 * @return
	 */
	public boolean addUserRoute(RouteDto routeDto, int timeOut);
	
	/**
	 * 重置超时时间
	 * @param routeDto
	 * @return
	 */
	public boolean pingUserRoute(RouteDto routeDto,int timeOut);
	
	/**
	 * 删除路由数据
	 * @param tenementId
	 * @param userId
	 * @param msg
	 */
	public boolean removeUserRoute(RouteDto routeDto,int timeOut);

}
