package com.easyim.biz.api.service.message;

import com.easyim.biz.api.dto.message.ForwardMsgDto;
import com.easyim.biz.api.dto.message.OfflineMsgDto;
import com.easyim.biz.api.dto.message.SendMsgDto;
import com.easyim.biz.api.dto.message.SendMsgResultDto;
import com.easyim.biz.api.dto.protocol.C2sProtocol;

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
	 * 给多个用户发送消息
	 * @param message
	 * @return
	 */
	public void sendMsg(SendMsgDto message,List<String> userIds);
	
	
	/**
	 * 拉取离线消息
	 * @return
	 */
	public List<C2sProtocol> pullOfflineMsg(OfflineMsgDto offlineMsgDto);
	
	/**
	 * 消息转发
	 */
	public void forwardMsg(ForwardMsgDto forwardMsgDto);
}
