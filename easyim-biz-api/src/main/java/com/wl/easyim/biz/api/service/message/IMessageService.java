package com.wl.easyim.biz.api.service.message;

import com.wl.easyim.biz.api.dto.message.MessageSendDto;
import com.wl.easyim.biz.api.dto.message.MessageSendResultDto;

/**
 * 消息服务
 * @author wl
 *
 */
public interface IMessageService {
	/**
	 * 发送消息
	 * @param message
	 * @return
	 */
	public MessageSendResultDto sendMessage(MessageSendDto message);
}
