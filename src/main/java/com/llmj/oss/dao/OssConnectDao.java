package com.llmj.oss.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.llmj.oss.model.OssConnect;

@Mapper
public interface OssConnectDao {
	
	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("insert into oss_connect ( endpoint,accessKeyId, accessKeySecret,bucketName,domain,detail) "
			+ "values ( #{endpoint},#{accessKeyId}, #{accessKeySecret}, #{bucketName}, #{domain}, #{detail})")
	int save(OssConnect oss);
	
	@Select("select * from oss_connect where id=#{id}")
	OssConnect selectById(@Param("id") int id);
	
	@Select("select * from oss_connect")
	List<OssConnect> getAll();
	
	@Update("update oss_connect set "
			+ "endpoint=#{endpoint},accessKeyId=#{accessKeyId},"
			+ "accessKeySecret=#{accessKeySecret},bucketName=#{bucketName},domain=#{domain},detail=#{detail} "
			+ "where id=#{id}")
	void update(OssConnect pack);
	
	@Delete("delete from oss_connect where id=#{id}")
	void delete(@Param("id") int id);
}
