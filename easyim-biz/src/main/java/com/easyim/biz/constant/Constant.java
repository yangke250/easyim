package com.easyim.biz.constant;

public abstract class Constant {
	//每次查询最大未同步消息数
	public final static int MAX_GET_OFFLINE_NUM = 50;
	
	public final static int MAX_RECENTLY_NUM = 100;
	
	// 离线消息，最多15天
	public final static int OFFLINE_TIME = 15 * 24 * 60 * 60;
	//编码格式
	public final static String CHARSET = "UTF-8";
	//消息id
	public static final String ID_KEY="easyim_msg_id";
	//消息离线id
	public static final String OFFLINE_MSG_SET_KEY = "easyim_msg_ids_offline_";
	//用户最近会话列表
	public static final String RECENTLY_KEY = "easyim_msg_recently_";
	//未读消息数
	public static final String UNREAD_MSG_KEY = "easyim_unread_";
	//离线消息
	public static final String OFFLINE_MSG_KEY = "easyim_msg_offline_";

	
}
