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
	
	@Select("select * from ${tableName} where gameId = #{gameId} and type = #{type} and state = 1 order by upTime desc")
	List<UploadFile> getFiles(@Param("tableName") String tableName,@Param("gameId") int gameId,@Param("state") int state,@Param("type") int type);
	
	@Update("update ${tableName} set state=#{file.state},operTime=now() where id=#{file.id}")
	void updateState1(@Param("tableName") String tableName,@Param("file") UploadFile file);
	
	@Update("update ${tableName} set state=#{file.state},operTime=now(),ossPath=#{file.ossPath} where id=#{file.id}")
	void delFile(@Param("tableName") String tableName,@Param("file") UploadFile file);
	
	@Update("update ${tableName} set state=#{after} where state=#{before} and gameId=#{file.gameId} and type=#{file.type}")
	void updateState(@Param("tableName") String tableName,@Param("before") int before,@Param("after") int after,@Param("file") UploadFile file);
	
	@Select("select * from ${tableName} where gameId = #{gameId} and type = #{type} and state = #{state}")
	List<UploadFile> selectOnline(@Param("tableName") String tableName,@Param("gameId") int gameId,@Param("state") int state,@Param("type") int type);
	
	@Select("select * from ${tableName} where gameId=#{gameId} and localPath=#{localPath}")
	UploadFile selectByLocalPath(@Param("gameId") int gameId,@Param("tableName") String tableName,@Param("localPath") String localPath);
	
	@Delete("delete from ${tableName} where id=#{id}")
	void deleteFile(@Param("id") int id,@Param("tableName") String tableName);
	
	@Update("update ${tableName} set notes=#{notes} where id=#{id}")
	void updateNotes(@Param("tableName") String tableName,@Param("id") int id,@Param("notes") String notes);
}
