package com.llmj.oss.manager;

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
	
	private String getTypeStr(int type) {
		if (type == IConsts.UpFileType.Android.getType()) {
			return IConsts.UpFileType.Android.getDesc();
		}
		return IConsts.UpFileType.Ios.getDesc();
	}
	
	/**
	 * 根据游戏id获得包名
	 * @param gameId
	 * @return
	 */
	public String getPackName(int gameId,int type) {
		PackageName pn = packDao.selectById(gameId);
		if (pn == null) {
			return "";
		}
		if (type == IConsts.UpFileType.Android.getType()) {
			return pn.getAndroid();
		}
		return pn.getIos();
	}
	
	/**
	 * 包名是否合理
	 * @param packName
	 * @return
	 */
	public boolean isContainPackage(String packName,int type) {
		 String str = getTypeStr(type);
		 PackageName pn = packDao.selectByType(str);
		 return pn != null;
	}
	
	/**
	 * 根据包名获得游戏id
	 * @param packName
	 * @return
	 */
	public int getGameIdByPack(String packName,int type) {
		 String str = getTypeStr(type);
		 PackageName pn = packDao.selectByType(str);
		 if (pn == null) {
			 return 0;
		 }
		 return pn.getGameId();
	}
}
