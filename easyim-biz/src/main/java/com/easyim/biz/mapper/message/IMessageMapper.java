package com.easyim.biz.mapper.message;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.easyim.biz.domain.ConversationDo;
import com.easyim.biz.domain.MessageDo;

@Mapper
public interface IMessageMapper {

	@Insert("insert into t_message (id,tenement_id,from_id,to_id,proxy_from_id,proxy_to_id,cid,proxy_cid,type,sub_type,content,biz_uid,gmt_create) values "
								+ "(#{m.id},#{m.tenementId},#{m.fromId},#{m.toId},#{m.proxyFromId},#{m.proxyToId},#{m.cid},#{m.proxyCid},#{m.type},#{m.subType},#{m.content},#{m.bizUid},#{m.gmtCreate})")
	@Options(useGeneratedKeys = true,keyProperty="id",keyColumn="id")  
	public long insertMessage(@Param("m")MessageDo mDo);
	
 
	@Select(
			" <script> "
			+ "select * from t_message where tenement_id=#{tenementId} and proxy_cid=#{proxyCid} "
    		+ "<if test=\"minMsgId>0\">"
			+ " and id < #{minMsgId} "
			+ "</if>"
			+ "<if test=\"cid>0\">"
			+ " and cid=#{cid} "
			+ "</if>"
			+ "order by id desc limit 10"
			+" </script> "
			)
	@Results(
    		id="msg",
    		value={
    				@Result(column="id",property="id",id=true),
    				@Result(column="tenement_id",property="tenementId"),
    				@Result(column="from_id",property="fromId"),
    				@Result(column="to_id",property="toId"),
    				@Result(column="proxy_from_id",property="proxyFromId"),
    				@Result(column="proxy_to_id",property="proxyToId"),
    				@Result(column="proxy_cid",property="proxyCid"),
    				@Result(column="cid",property="cid"),
    				@Result(column="type",property="type"),
    				@Result(column="sub_type",property="subType"),
    				@Result(column="content",property="content"),
    				@Result(column="biz_uid",property="bizUid"),
    				@Result(column="gmt_create",property="gmtCreate")
    				}
    		)
	public List<MessageDo> selectMessage(@Param("tenementId")long tenementId,@Param("proxyCid")long proxyCid,@Param("cid")long cid,@Param("minMsgId")long minMsgId);
}
