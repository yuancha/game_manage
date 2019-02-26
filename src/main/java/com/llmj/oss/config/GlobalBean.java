package com.llmj.oss.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.llmj.oss.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置文件管理类
 * @author xinghehudong
 *
 */
@Component
@Slf4j(topic = "ossLogger")
public class GlobalBean {
	/**
	 * 游戏id 对应 唯一包名
	 */
	private Map<String,String> gamePackage;
	
	@PostConstruct
	private void init() {
		gamePackage = FileUtil.LoadPopertiesFile("config/gamePackage.properties");
		log.info("============>初始加载properties文件成功");
	}
	/**
	 * 根据游戏id获得包名
	 * @param gameId
	 * @return
	 */
	public String getPackName(String gameId) {
		return gamePackage.get(gameId);
	}
	
	/**
	 * 包名是否合理
	 * @param packName
	 * @return
	 */
	public boolean isContainPackage(String packName) {
		Set<String> tmp = new HashSet<String>(gamePackage.values());
		return tmp.contains(packName);
	}
}
