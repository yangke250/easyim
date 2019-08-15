package com.easyim.biz.service.user.impl;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.easyim.biz.api.service.user.IUserService;
import com.easyim.route.service.IUserRouteService;

@Service(interfaceClass=IUserService.class)
public class UserServiceImpl implements IUserService {

	@Resource
	private IUserRouteService userRouteService;
	
	@Override
	public List<String> selectUserOnline(long tenementId, List<String> userIds) {

		return userRouteService.getOnlineUsers(tenementId, userIds);
	}

}
