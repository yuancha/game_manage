package com.llmj.oss.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.llmj.oss.model.GameControl;

@Mapper
public interface GameControlDao {
	
	@Insert("insert into game_control ( gameId,name, open,channel,ossId) "
			+ "values ( #{gameId},#{name}, #{open}, #{channel}, #{ossId})")
	int save(GameControl game);
	
	@Select("select * from game_control where gameId=#{gameId}")
	GameControl selectById(@Param("gameId") int gameId);
	
	@Select("select * from game_control")
	List<GameControl> getAll();
	
	@Update("update game_control set "
			+ "name=#{name},open=#{open},channel=#{channel},ossId=#{ossId} "
			+ "where gameId=#{gameId}")
	void update(GameControl pack);
	
	@Select("select * from game_control where open=1")
	List<GameControl> selectOpens();//查看已开放游戏
	
	@Select("select * from game_control where ossId = #{ossId}")
	List<GameControl> selectByOssId(@Param("ossId") int ossId);
}
