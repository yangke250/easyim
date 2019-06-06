package com.easyim.biz.mapper.conversation;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.easyim.biz.domain.ConversationDo;

@Mapper
public interface IConversationMapper {
	
	@Select("select * from t_conversation where tenement_id = #{tenementId} and small_id=#{smallId} and big_id=#{bigId}")
	public ConversationDo getConversation(
			@Param("tenementId") long tenementId,
			@Param("smallId") String smallId,
			@Param("bigId") String bigId);
	
	@Insert("insert into t_conversation (tenement_id,small_id,big_id,proxy_cid) values (#{c.tenementId},#{c.smallId},#{c.bigId},#{c.proxyCid})")
	@Options(useGeneratedKeys = true,keyProperty="id",keyColumn="id") // Adding this line instread of @SelectKey 
	public long insertConversationDo(@Param("c")ConversationDo cDo);
}
