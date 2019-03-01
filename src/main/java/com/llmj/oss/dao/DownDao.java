package com.llmj.oss.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.llmj.oss.model.DownLink;

@Mapper
public interface DownDao {
	
	@Insert("insert into down_link ( id,type, link,upTime) "
			+ "values ( #{id},#{type}, #{link}, now()) "
			+ "on duplicate key update link=values(link),upTime=values(upTime)")
	int saveLink(DownLink dl);
	
	@Select("select * from down_link where id=#{id} and type=#{type}")
	DownLink selectById(@Param("id") String id,@Param("type") int type);
	
}
