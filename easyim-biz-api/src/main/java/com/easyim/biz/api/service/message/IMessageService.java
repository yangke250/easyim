package com.easyim.biz.api.service.message;

import com.easyim.biz.api.dto.message.OfflineMsgDto;
import com.easyim.biz.api.dto.message.SendMsgDto;
import com.easyim.biz.api.dto.message.SendMsgResultDto;
import com.easyim.biz.api.protocol.c2s.MessagePush;
import com.easyim.biz.api.protocol.enums.c2s.ResourceType;

import java.util.List;


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
	public SendMsgResultDto sendMsg(SendMsgDto message);
	
	
	/**
	 * 拉取离线消息
	 * @return
	 */
	public List<MessagePush> pullOfflineMsg(OfflineMsgDto offlineMsgDto);
}
