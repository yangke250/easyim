package com.wl.easyim.biz.api.service.message;

import com.wl.easyim.biz.api.dto.message.SendMsgDto;
import com.wl.easyim.biz.api.dto.message.SendMsgResultDto;
import com.wl.easyim.biz.api.dto.message.OfflineMsgDto;
import com.wl.easyim.biz.api.protocol.enums.c2s.ResourceType;
import com.wl.easyim.biz.api.protocol.protocol.c2s.MessagePush;

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
