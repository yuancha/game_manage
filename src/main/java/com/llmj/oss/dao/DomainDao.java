package com.llmj.oss.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.llmj.oss.model.Domain;

@Mapper
public interface DomainDao {
	
/*	@Insert("insert into down_link ( id,type, link,upTime) "
			+ "values ( #{id},#{type}, #{link}, now()) "
			+ "on duplicate key update link=values(link),upTime=values(upTime)")
	int saveLink(DownLink dl);*/
	
	@Select("select * from domain_tb where domain=#{domain}")
	Domain selectById(@Param("domain") String domain);
	
	@Select("select * from domain_tb where type=#{type}")
	List<Domain> selectByType(@Param("type") int type);
	
}
