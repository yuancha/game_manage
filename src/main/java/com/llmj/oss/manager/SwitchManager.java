package com.llmj.oss.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.RedisConsts;
import com.llmj.oss.dao.DomainDao;
import com.llmj.oss.dao.QrcodeDao;
import com.llmj.oss.model.Domain;
import com.llmj.oss.model.QRCode;
import com.llmj.oss.util.RedisTem;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 开关管理类
 * @author xinghehudong
 *
 */
@Component
@Slf4j(topic = "ossLogger")
public class SwitchManager {
	
	@Value("${upload.local.basePath}")
	private String localPath;
	
	@Autowired
	private AliOssManager ossMgr;
	@Autowired
	private QrcodeDao qrDao;
	@Autowired
	private DomainDao domainDao;
	@Autowired
	private RedisTem redis;
	
    public boolean getOssSwitch(int gameId) {
    	boolean ossSuccess = true;	//默认oss正常 是开启的
    	try {
			String onoff = redis.hget(RedisConsts.OSS_SWITCH_KEY,String.valueOf(gameId));
			if (!StringUtil.isEmpty(onoff)) {
				ossSuccess = Boolean.parseBoolean(onoff);
			}
		} catch (Exception e) {
			log.error("getOssSwitch error,gameId:{},exception->{}",gameId,e);
		}
    	return ossSuccess;
    }
    
    public String getQrcodeLink(QRCode qr) throws Exception {
    	String link = "";
    	if (getOssSwitch(qr.getGameId())) {
    		link = ossMgr.ossDomain(qr.getGameId()) + "/" + qr.getOssPath();
    	} else {
    		//本地连接
    		String domain = getUseDomain(qr.getGameId(),qr.getState());
    		if (!StringUtil.isEmpty(domain)) {
    			link = domain + IConsts.LOCALDOWN + qr.getLocalPath().substring(localPath.length());
    		}
    	}
    	if (StringUtil.isEmpty(link)) {
    		log.error("qrcode link error,qrCode : {}",StringUtil.objToJson(qr));
    	}
    	qr.setOssPath(link);
    	return link;
    }
    
    /**
     * 获得当前游戏正在使用的域名
     * @return
     */
    public String getUseDomain(int gameId,int state) throws Exception {
    	String domain = "";
    	Domain domains = domainDao.selectByType(0);
    	if (domains == null) {
    		log.error("域名数据表为空");
    		return domain;
    	}
    	String tmp = domains.getDomain();
    	if (state == 1) {
    		QRCode code = qrDao.selectByLogicUse(gameId,1,state);
    		//正式服 获得正式服二维码使用 的域名
    		if (code != null) {
    			tmp = code.getLink();
    		}
    	} 
    	domain = tmp.substring(0,tmp.indexOf("/",8));
    	return domain;
    }
    
    public void changeOssSwitch(int gameId,boolean onoff) throws Exception {	//true 开启oss下载 false开启本地下载
    	if (getOssSwitch(gameId) == onoff) {
    		return;
    	}
    	//redis.hset(RedisConsts.OSS_SWITCH_KEY, String.valueOf(gameId), String.valueOf(onoff));
    	//TODO 系列操作
    	//删除redis 下载链接
    	//通知所有逻辑服 二维码地址连接改变
    }
    
    /**
     * 悟空vip ios签名总开关
     * 无或0 为开 1为关
     */
    public boolean VipLinkSwitch() throws Exception {
    	String flag = redis.get(RedisConsts.VIP_LINK_SWITCH_KEY);
    	if (StringUtil.isEmpty(flag) || Integer.parseInt(flag) == 0) {
    		return true;
    	}
    	return false;
    }
}
