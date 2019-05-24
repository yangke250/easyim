package com.wl.easyim.biz.mapper.conversation;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.wl.easyim.biz.domain.ConversationDo;

@Mapper
public interface IConversationMapper {
	
	@Select("select * from t_conversation where tenement_id = #{tenementId} and small_id=#{smallId} and big_id=#{bigId}")
	public ConversationDo getConversation(
			@Param("tenementId") long tenementId,
			@Param("smallId") String smallId,
			@Param("bigId") String bigId);
	
	@Insert("insert into t_conversation tenement_id,small_id,big_id,proxy_cid values (#{cDo.tenementId,cDo.smallId,cDo.bigId,cDo.proxyCid}")
	public long createConversationDo(@Param("cDo")ConversationDo cDo);
}
