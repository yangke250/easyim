package com.wl.easyim.biz.api.service.user;

import com.wl.easyim.biz.api.dto.user.UserAuthDto;
import com.wl.easyim.biz.api.dto.user.UserSessionDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.ResourceType;

public interface IUserService {
	
	/**
	 * 得到im的token
	 * @param tenementId
	 * @param userId
	 * @param resoureType
	 * @return
	 */
	public String authEncode(long tenementId,String userId,ResourceType resoureType);
	
	/**
	 * 解码jwt code
	 * @param jwt
	 * @return
	 */
	public UserAuthDto authDecode(String jwt);
}
