package com.easyim.biz.mapper.message;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.easyim.biz.domain.ConversationDo;
import com.easyim.biz.domain.MessageDo;

@Mapper
public interface IMessageMapper {

	@Insert("insert into t_message (id,tenement_id,from_id,to_id,proxy_from_id,proxy_to_id,cid,proxy_cid,type,sub_type,content,biz_uid) values (#{m.id},#{m.tenementId},#{m.fromId},#{m.toId},#{m.proxyFromId},#{m.proxyToId},#{m.proxyCid},#{m.cid},#{m.type},#{m.subType},#{m.content},#{m.bizUid})")
	public long insertMessage(@Param("m")MessageDo mDo);

}
