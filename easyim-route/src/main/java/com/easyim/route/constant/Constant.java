package com.easyim.route.constant;

public abstract class Constant {
	
	public final static String ROUTE_STRING_PRE ="easyim_route_s";
	
	public final static String ROUTE_HASH_PRE   ="easyim_route_h";
	
	public final static String LOGIN_STRING_PRE ="easyim_login_s";
	
	public final static String SPLIT="_";

	/**
	 * 用户登录时间
	 * @param uid
	 * @return
	 */
	public static String getLoginString(String uid){
		return LOGIN_STRING_PRE+uid;
	}
	
	/**
	 * 用户登录服务器的key
	 * @param uid
	 * @return
	 */
	public static String getRouteString(String uid){
		return ROUTE_STRING_PRE+uid;
	}
	
	/**
	 * 用户对应的链接id的列表
	 * @param uid
	 * @return
	 */
	public static String getRouteHash(String uid){
		return ROUTE_HASH_PRE+uid;
	}
	
	/**
	 * 得到uid
	 * @param tenementId
	 * @param userId
	 * @return
	 */
	public static String getUid(long tenementId,String userId){
		return tenementId+SPLIT+userId;
	}
}
