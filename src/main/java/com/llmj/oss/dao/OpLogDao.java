package com.llmj.oss.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import com.llmj.oss.model.OpLog;

@Mapper
public interface OpLogDao {
	
	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("insert into op_log ( op_account, op_type,op_content ,op_time) "
			+ "values ( #{op_account},#{op_type}, #{op_content}, #{op_time})")
	int saveLog(OpLog log);
	
	/*@Select("select * from down_link where id=#{id}")
	DownLink selectById(@Param("id") String id);*/
	
}
