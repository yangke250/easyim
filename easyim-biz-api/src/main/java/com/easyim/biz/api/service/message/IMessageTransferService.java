package com.easyim.biz.api.service.message;

import com.easyim.biz.api.dto.message.SendMsgDto;

/**
 * 消息数据迁移
 * @author wl
 *
 */
public interface IMessageTransferService {
	
	/**
	 * 迁移消息
	 * @param sendMsgDto
	 */
	public void transferMessage(long id,SendMsgDto sendMsgDto);
}
