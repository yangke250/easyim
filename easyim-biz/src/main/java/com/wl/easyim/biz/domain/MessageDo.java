package com.wl.easyim.biz.domain;

import java.util.Date;

import lombok.Data;

@Data
public class MessageDo {
	private long id;
	private long tenementId;//租户id
	private String fromId;//消息from
	private String toId;//消息接收方
	private String proxyFromId;//发送方路由的代理
	private String proxyToId;//消息接受路由的代理
	private long proxyCid;
	private long cid;
	private int type;//1 文本  
	private int subType;
	private String content;
	private Date gmtCreate;
	private String ticket;//消息唯一业务码

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
