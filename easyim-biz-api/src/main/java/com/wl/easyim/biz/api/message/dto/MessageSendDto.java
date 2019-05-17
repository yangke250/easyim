package com.wl.easyim.biz.api.message.dto;


import lombok.Data;

@Data
public class MessageSendDto {
	
	private long tenementId;
	private String fromId;
	private String fromProxyId;
	private String toId;
	private String toProxyId; 
	private long cid;
	private long proxyCid;
	private MessageType type;
	private int subType;

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
