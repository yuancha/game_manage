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
	
	@Options(useGeneratedKeys = true, keyProperty = "file.id")
	@Insert("insert into ${tableName} ( game, packName, vision, type , state, upTime, localPath, ossPath,fileName,gameId) "
			+ "values( #{file.game}, #{file.packName}, #{file.vision}, #{file.type}, #{file.state}, now(), "
			+ "#{file.localPath},#{file.ossPath},#{file.fileName},#{file.gameId})")
	int saveFile(@Param("file") UploadFile file,@Param("tableName") String tableName);
	
	@Options(useGeneratedKeys = true, keyProperty = "file.id")
	@Insert("insert into ${tableName} ( game, packName, vision, type , state, upTime, localPath, ossPath, operTime,fileName,gameId) "
			+ "values( #{file.game}, #{file.packName}, #{file.vision}, #{file.type}, #{file.state},"
			+ " #{file.upTime}, #{file.localPath},#{file.ossPath},now(),#{file.fileName},#{file.gameId})")
	int copyFile(@Param("file") UploadFile file,@Param("tableName") String tableName);
	
	@Select("select * from ${tableName} where id=#{id}")
	UploadFile selectById(@Param("id") int id,@Param("tableName") String tableName);
	
	@Select("select * from ${tableName} where packName = #{packName} and type = #{type} and state != #{state} order by upTime desc")
	List<UploadFile> getFiles(@Param("tableName") String tableName,@Param("packName") String packName,@Param("state") int state,@Param("type") int type);
	
	@Update("update ${tableName} set state=#{file.state},operTime=now() where id=#{file.id}")
	void updateState1(@Param("tableName") String tableName,@Param("file") UploadFile file);
	
	@Update("update ${tableName} set state=#{file.state},operTime=now(),ossPath=#{file.ossPath} where id=#{file.id}")
	void delFile(@Param("tableName") String tableName,@Param("file") UploadFile file);
	
	@Update("update ${tableName} set state=#{after} where state=#{before} and packName=#{file.packName} and type=#{file.type}")
	void updateState(@Param("tableName") String tableName,@Param("before") int before,@Param("after") int after,@Param("file") UploadFile file);
	
	@Select("select * from ${tableName} where packName = #{packName} and type = #{type} and state = #{state}")
	List<UploadFile> selectOnline(@Param("tableName") String tableName,@Param("packName") String packName,@Param("state") int state,@Param("type") int type);
}
