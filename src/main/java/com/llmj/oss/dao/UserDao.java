package com.llmj.oss.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.llmj.oss.model.Role;
import com.llmj.oss.model.Power;
import com.llmj.oss.model.User;

@Mapper
public interface UserDao {
	
	@Select("select * from user where account=#{acc} and password=#{pwd}")
	User selectUse(@Param("acc") String account,@Param("pwd") String password);
	
	@Update("update user set loginTime=now() where account=#{account} ")
	void updateLoginTime(User user);
	
	@Select("select * from role where role=#{role}")
	Role selectRole(@Param("role") int role);
	
	@Select("select * from power")
	List<Power> selectPowers();
	
	@Select("select * from power where id = #{id}")
	Power selectPowersById(@Param("id") int id);
	
	@Select("select * from user where account=#{acc}")
	User selectUseByAccount(@Param("acc") String account);
}
