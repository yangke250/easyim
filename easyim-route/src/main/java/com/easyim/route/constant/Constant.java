package com.easyim.route.constant;

public abstract class Constant {
	
	public final static String ROUTE_STRING_PRE ="easyim_rote_s";
	
	public final static String ROUTE_HASH_PRE   ="easyim_rote_h";
	
	public final static String SPLIT="_";


	public static String getRouteString(String uid){
		return ROUTE_STRING_PRE+uid;
	}
	
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
