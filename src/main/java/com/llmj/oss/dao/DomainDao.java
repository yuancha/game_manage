package com.llmj.oss.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.llmj.oss.model.Domain;

@Mapper
public interface DomainDao {
	
	@Insert("insert into domain_tb ( domain, type,content) "
			+ "values ( #{domain},#{type}, #{content})")
	int save(Domain dl);
	
	@Select("select * from domain_tb where domain=#{domain}")
	Domain selectById(@Param("domain") String domain);
	
	@Select("select * from domain_tb where type=#{type}")
	List<Domain> selectByType(@Param("type") int type);
	
	@Select("select * from domain_tb")
	List<Domain> getAll();
	
	@Update("update domain_tb set type=#{type}, content=#{content} where domain=#{domain}")
	void updateDomain(Domain dl);
	
	@Delete("delete from domain_tb where domain=#{domain}")
	void delDomain(Domain dl);
}
