package com.easyim.biz.api.service.user;

import com.easyim.biz.api.dto.user.UserAuthDto;
import com.easyim.biz.api.dto.user.UserConnectDto;
import com.easyim.biz.api.dto.user.UserSessionDto;
import com.easyim.biz.api.protocol.enums.c2s.ResourceType;

/**
 * 用户相关接口
 * @author wl
 *
 */
public interface IUserAuthService {
	
	/**
	 * 得到连接相关信息
	 * @param tenementId
	 * @param userId
	 * @param resoureType
	 */
	public UserConnectDto getConnectInfo(UserAuthDto userAuthDto);
	
	/**
	 * 得到im的token
	 * @param tenementId
	 * @param userId
	 * @param resoureType
	 * @return
	 */
	public String authEncode(UserAuthDto userAuthDto);
	
	/**
	 * 解码jwt code
	 * @param jwt
	 * @return
	 */
	public UserAuthDto authDecode(String jwt);
}
