package com.llmj.oss.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.IConsts.UpFileType;
import com.llmj.oss.dao.PackageDao;
import com.llmj.oss.model.PackageName;
import com.llmj.oss.util.FileUtil;

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
	private Map<Integer,String> androidPack = new ConcurrentHashMap<>();
	private Map<Integer,String> iosPack = new ConcurrentHashMap<>();
	
	//FileUtil.LoadPopertiesFile("config/gamePackage.properties");
	
	@PostConstruct
	public void packNameInit() {
		if (androidPack != null) androidPack.clear();
		if (iosPack != null) iosPack.clear();
		
		List<PackageName> list = packDao.getAll();
		for (PackageName pn : list) {
			int gameId = pn.getGameId();
			androidPack.put(gameId, pn.getAndroid());
			iosPack.put(gameId, pn.getIos());
		}
		
		log.info("game package android map -> {}",androidPack);
		log.info("game package ios map -> {}",iosPack);
		log.info("============>初始加载pack_name成功");
	}
	
	/**
	 * 根据游戏id获得包名
	 * @param gameId
	 * @return
	 */
	public String getPackName(int gameId,int type) {
		if (type == IConsts.UpFileType.Android.getType()) {
			return androidPack.get(gameId);
		}
		return iosPack.get(gameId);
	}
	
	/**
	 * 包名是否合理
	 * @param packName
	 * @return
	 */
	public boolean isContainPackage(String packName,int type) {
		Set<String> tmp = null;
		if (type == IConsts.UpFileType.Android.getType()) {
			tmp = new HashSet<String>(androidPack.values());
		} else {
			tmp = new HashSet<String>(iosPack.values());
		}
		
		return tmp.contains(packName);
	}
	
	/**
	 * 根据包名获得游戏id
	 * @param packName
	 * @return
	 */
	public int getGameIdByPack(String packName,int type) {
		int gid = 0;
		if (type == IConsts.UpFileType.Android.getType()) {
			for (int id : androidPack.keySet()) {
				if (androidPack.get(id).equals(packName)) {
					gid = id;
					break;
				}
			}
		} else {
			for (int id : iosPack.keySet()) {
				if (iosPack.get(id).equals(packName)) {
					gid = id;
					break;
				}
			}
		}
		return gid;
	}
	
	/**
	 * 刷新对应包名
	 * @param pn
	 */
	public void refreshPack(PackageName pn) {
		androidPack.put(pn.getGameId(), pn.getAndroid());
		log.info("refresh android pack,gameId:{},packName : {}",pn.getGameId(), pn.getAndroid());
		iosPack.put(pn.getGameId(), pn.getIos());
		log.info("refresh ios pack,gameId:{},packName : {}",pn.getGameId(), pn.getIos());
	}
	
	/**
	 * 删除对应包名
	 * @param gameId
	 */
	public void delPack(int gameId) {
		androidPack.remove(gameId);
		log.info("delPack android pack,gameId:{}",gameId);
		iosPack.remove(gameId);
		log.info("delPack ios pack,gameId:{}",gameId);
	}
}
