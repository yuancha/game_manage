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
	
	private Set<String> urlFilter;	//不需要拦截的url
	
	
	@PostConstruct
	private void init() {
		try {
			urlFilter = FileUtil.readTxtFile("config/urlFilter.txt");
			log.info("webDomains load success, -> {}",urlFilter);
		} catch (Exception e) {
			log.error("GlobalConfig init error,Exception -> {}",e);
		}
		
		log.info("============>初始加载配置文件成功");
	}
	
	/**
	 * 是否需要拦截
	 * @param domain
	 * @return
	 */
	public boolean notIntercept (String url) {
		return urlFilter.contains(url);
	}
	
}

