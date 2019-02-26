package com.llmj.oss.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.llmj.oss.model.UploadFile;

@Mapper
public interface UploadDao {
	
	// 插入货币类型
	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("insert into upload_file ( game, packName, vision, type , state, upTime, localPath) "
			+ "values( #{game}, #{packName}, #{vision}, #{type}, #{state}, now(), #{localPath})")
	int saveLog(UploadFile file);
	
	@Select("select * from upload_file where id=#{id}")
	UploadFile selectById(@Param("id") int id);
	
	@Select("select * from upload_file where packName = #{packName} and type = #{type} and state != #{state}")
	List<UploadFile> getFiles(@Param("packName") String packName,@Param("state") int state,@Param("type") int type);
	
	@Update("update upload_file set state=#{state},operTime=now(),ossPath=#{ossPath} where id=#{id}")
	void upToOss(UploadFile file);
	
	@Update("update upload_file set state=#{state},operTime=now(),ossPath=#{ossPath} where id=#{id}")
	void delFile(UploadFile file);
	
	@Update("update upload_file set state=#{after} where state=#{before} and packName=#{file.packName} and type=#{file.type}")
	void updateState(@Param("before") int before,@Param("after") int after,@Param("file") UploadFile file);
}
