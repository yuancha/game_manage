package com.llmj.oss.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.llmj.oss.model.QRCode;

@Mapper
public interface QrcodeDao {
	
	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("insert into qr_code ( gameId, state, content, link, photo, ossPath,localPath) "
			+ "values( #{gameId}, #{state}, #{content}, #{link}, #{photo} , #{ossPath} , #{localPath})")
	int save(QRCode qr);
	
	@Select("select * from qr_code where id=#{id}")
	QRCode selectById(@Param("id") int id);
	
	@Select("select * from qr_code where gameId = #{gameId} and state = #{state} ")
	List<QRCode> getQRs(@Param("gameId") int gameId,@Param("state") int state);
	
	@Delete("delete from qr_code where link=#{link}")
	void delQR(@Param("link") String link);
	
	/*@Update("update qr_code set link=#{link} , content=#{content} , desc=#{desc} where id=#{id} ")
	void updateQr(QRCode qr);*/
	
	@Select("select * from qr_code where link = #{link}")
	QRCode selectByLink(@Param("link") String link);
	
	@Update("update qr_code set logicUse=#{logicUse} where id=#{id} ")
	void updateLogicUse(QRCode qr);
	
	@Select("select * from qr_code where gameId=#{gameId} and logicUse=#{logicUse} and state=#{state}")
	QRCode selectByLogicUse(@Param("gameId") int gameId,@Param("logicUse") int logicUse,@Param("state") int state);
	
	@Select("select content,link,ossPath from qr_code where logicUse = #{logicUse} and state = #{state} ")
	List<QRCode> getOnlineQRs(@Param("logicUse") int logicUse,@Param("state") int state);
}
