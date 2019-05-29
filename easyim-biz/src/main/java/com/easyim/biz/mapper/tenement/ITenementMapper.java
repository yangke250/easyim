package com.easyim.biz.mapper.tenement;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.easyim.biz.domain.TenementDo;

@Mapper
public interface ITenementMapper {
	/**
	 * 查询相关租户
	 * @param id
	 * @return
	 */
	@Select("select * from t_tenement where id=#{id}")
	public TenementDo getTenementById(@Param("id") long id);
}
