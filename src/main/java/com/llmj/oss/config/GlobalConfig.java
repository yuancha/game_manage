package com.llmj.oss.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.llmj.oss.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局配置
 * @author xinghehudong
 *
 */
@Component
@Slf4j(topic = "ossLogger")
public class GlobalConfig {
	/**
	 * 游戏id 对应 唯一包名
	 */
	private Set<String> webDomains;	//web服务器域名
	//oss服务器域名
	
	@PostConstruct
	private void init() {
		/*try {
			webDomains = FileUtil.readTxtFile("config/webdomain.txt");
			log.info("webDomains load success, -> {}",webDomains);
		} catch (Exception e) {
			log.error("GlobalConfig init error,Exception -> {}",e);
		}*/
		
		log.info("============>初始加载配置文件成功");
	}
	
	/**
	 * 域名是否有效
	 * @param domain
	 * @return
	 */
	public boolean webDomainContains(String domain) {
		return webDomains.contains(domain);
	}
	
	public Set<String> getWebDomains() {
		return webDomains;
	}
}

