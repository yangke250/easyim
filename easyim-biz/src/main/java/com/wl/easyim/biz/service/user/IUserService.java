package com.wl.easyim.biz.service.user;

import com.wl.easyim.biz.api.protocol.c2s.enums.ResourceType;
import com.wl.easyim.biz.api.protocol.s2s.dto.UserDto;
import com.wl.easyim.biz.bo.UserBo;

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
	public UserBo authDecode(String jwt);
}
