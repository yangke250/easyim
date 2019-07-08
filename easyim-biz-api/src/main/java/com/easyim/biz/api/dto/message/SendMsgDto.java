package com.easyim.biz.api.dto.message;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

//import javax.validation.constraints.Min;
//import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SendMsgDto {
	
	@Min(value = 0)
	private long tenementId;
	@NotNull
	private String fromId;
	
	private String proxyFromId;
	@NotNull
	private String toId;

	private String proxyToId; 
	@Min(value = 0)
	private long cid;
	@Min(value = 0)
	private long proxyCid;
	@NotNull
	private MessageType type;
	@NotNull
	private String subType;
	@NotNull
	private String content;

	private String bizUid;//业务方唯一id
	
	/**
	 * 客户端根据messageType解析推送的内容
	 * @author wl
	 *
	 */
	public static enum MessageType{
		text(0),//文本
		pic(1),//图片
		voice(2),//声音
		notify(3),//系统通知,不落库,只走离线消息
		file(4);//文件
		
		MessageType(int value){
			this.value = value;
		}
		private int value;
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		
		
		public static MessageType getMessageType(int value){
			MessageType[] types = MessageType.values();
			for(MessageType t:types){
				if(t.getValue()==value){
					return t;
				}
			}
			throw new IllegalArgumentException("value:"+value);
		}
	}
}
