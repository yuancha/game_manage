package com.llmj.oss.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.llmj.oss.config.IConsts;
import com.llmj.oss.dao.PackageDao;
import com.llmj.oss.model.PackageName;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置文件管理类
 * @author xinghehudong
 *
 */
@Component
@Slf4j(topic = "ossLogger")
public class PackManager {
	
	@Autowired
	private PackageDao packDao;
	
	/**
	 * 游戏id 对应 唯一包名
	 */
//	private Map<Integer,String> androidPack = new ConcurrentHashMap<>();
//	private Map<Integer,String> iosPack = new ConcurrentHashMap<>();
	
	//FileUtil.LoadPopertiesFile("config/gamePackage.properties");
	
	/*@PostConstruct
	public void packNameInit() {
		
	}*/
	
	/**
	 * 根据游戏id获得包名
	 * @param gameId
	 * @return
	 */
//	public String getPackName(int gameId,int type) {
//		PackageName pn = packDao.selectById(gameId);
//		if (pn == null) {
//			return "";
//		}
//		if (type == IConsts.UpFileType.Android.getType()) {
//			return pn.getAndroid();
//		}
//		return pn.getIos();
//	}
	
	/**
	 * 获得包信息
	 * @param packName
	 * @return
	 */
	public PackageName getPackInfo(String packName,int type, String gameName) {
		PackageName pn = null;
		try {
			
			pn = packDao.selectByGameName(gameName);
			
			if (pn != null) {
				return pn;
			}
			List<PackageName> list = new ArrayList<>();
			if (type == IConsts.UpFileType.Android.getType()) {
				list = packDao.selectByAndroid(packName);
			} else {
				list = packDao.selectByIos(packName);
			}
			if (list.size() == 1) {//只有一个
				pn = list.get(0);
			} 
		} catch (Exception e) {
			log.debug("查找包信息失败 e : {}", e);
		}
		return pn;
	}
	
}
