package com.llmj.oss.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.llmj.oss.model.PackageName;

@Mapper
public interface PackageDao {
	
	@Insert("insert into package_name ( gameId,content, android,ios) "
			+ "values ( #{gameId},#{content}, #{android}, #{ios})")
	int savePack(PackageName pack);
	
	@Select("select * from package_name where gameId=#{gameId}")
	PackageName selectById(@Param("gameId") int gameId);
	
	/*根据安卓或是ios包名查找类*/
	@Select("select * from package_name where ios=#{pack}")
	List<PackageName> selectByIos(@Param("pack") String pack);
	
	@Select("select * from package_name where android=#{pack}")
	List<PackageName> selectByAndroid(@Param("pack") String pack);
	
	@Select("select * from package_name order by gameId")
	List<PackageName> getAll();
	
	@Update("update package_name set content=#{content},android=#{android},ios=#{ios} where gameId=#{gameId}")
	void updatePack(PackageName pack);
	
	@Delete("delete from package_name where gameId=#{gameId}")
	void delPack(@Param("gameId") int gameId);
	
	@Select("select * from package_name where content=#{content}")
	PackageName selectByGameName(@Param("content") String gameName);
}
