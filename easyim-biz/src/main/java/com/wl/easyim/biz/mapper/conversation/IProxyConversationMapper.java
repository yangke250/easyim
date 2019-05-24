package com.wl.easyim.biz.mapper.conversation;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.wl.easyim.biz.domain.ConversationDo;
import com.wl.easyim.biz.domain.ProxyConversationDo;

@Mapper
public interface IProxyConversationMapper {
	
	@Select("select * from t_proxy_conversation where tenement_id = #{tenementId} and proxy_small_id=#{proxySmallId} and proxy_big_id=#{proxyBigId}")
	public ProxyConversationDo getProxyConversation(
			@Param("tenementId") long tenementId,
			@Param("proxySmallId") String proxySmallId,
			@Param("proxyBigId") String proxyBigId);
	
	@Insert("insert into t_proxy_conversation tenement_id,proxy_small_id,proxy_big_id values (#{cDo.tenementId,cDo.proxySmallId,cDo.proxyBigId}")
	public long insertProxyConversationDo(@Param("cDo")ProxyConversationDo cDo);
}
