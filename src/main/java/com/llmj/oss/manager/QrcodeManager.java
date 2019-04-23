package com.llmj.oss.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.RedisConsts;
import com.llmj.oss.dao.QrcodeDao;
import com.llmj.oss.model.QRCode;
import com.llmj.oss.util.FileUtil;
import com.llmj.oss.util.QRCodeUtil;
import com.llmj.oss.util.RedisTem;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * qr管理类
 * @author xinghehudong
 *
 */
@Component
@Slf4j(topic = "ossLogger")
public class QrcodeManager {
	
	@Value("${upload.local.logo}")
	private String logoPath;
	@Value("${upload.oss.test}")
    public String ossTest;
    @Value("${upload.oss.online}")
    public String ossOnline;
    @Value("${upload.local.qrcode}")
    public String qrcodePath;
    @Value("${upload.local.basePath}")
	private String localPath;
    
	@Autowired
	private QrcodeDao qrDao;
	@Autowired
	private AliOssManager ossMgr;
	@Autowired
	private MqManager mqMgr;
	@Autowired 
	private RedisTem redis;
	
	public QRCode getUseQrcode(int gameId,int logic,int state) {
		return qrDao.selectByLogicUse(gameId, logic, state);
	}
	
	public void qrCodeDel(QRCode qr) throws Exception {
		qrDao.delQR(qr.getLink());
		ossMgr.removeFile(qr.getOssPath(),qr.getGameId());
		FileUtil.deleteFile(qr.getLocalPath());
		log.info("二维码删除成功，link : {}",qr.getLink());
	}
	
	public String saveQr(QRCode qr,String domain,int middle,HttpServletRequest request) throws Exception {
		int gameId = qr.getGameId();
		int state = qr.getState();
		String link = domain + "?gameId="+gameId+"&gameState="+state;
		
		if (qrDao.selectByLink(link) != null) {
			return link;
		}
		InputStream is = null;
		InputStream is2 = null;
		try {
			qr.setLink(link);
			if (middle == 1) {
				is = getMiddelImg(gameId, request);
			}
			byte[] tmp = QRCodeUtil.encode(link,is);
			/*String arryStr = Arrays.toString(tmp);
			arryStr = arryStr.substring(1, arryStr.length() - 1);*/
			qr.setPhoto("");
			//qr.setPhoto(arryStr);
			
			//保存到oss
			String qrName = StringUtil.getUUIDStr();
			String qrSavePath = qrOssPath(state,qrName);
			ossMgr.uploadFileByByte(qrSavePath, tmp,gameId);
			qr.setOssPath(qrSavePath);
			
			//保存到本地
			if (middle == 1) {
				is2 = getMiddelImg(gameId, request);
			}
			QRCodeUtil.encode(link,qrcodePath,qrName,is2);
			qr.setLocalPath(qrcodePath + qrName + ".png");
			
			if (state == 1) {//正式数据通知逻辑服
				qr.setLogicUse(1);
				//String qrLink = switchMgr.getQrcodeLink(qr);
				noticeLogicServer(qr, domain);
			}
			qrDao.save(qr);
		} finally {
			if (is != null) {
				is.close();
			}
			if (is2 != null) {
				is2.close();
			}
		}
		return "";
	}
	
	//获得二维码中间图片
	private InputStream getMiddelImg(int gameId, HttpServletRequest request) {
		InputStream is = null;
		try {
			String path = redis.hgetPrefix(RedisConsts.PRE_HTML_KEY, RedisConsts.ICON_PATH_KEY, String.valueOf(gameId));
			if (StringUtil.isEmpty(path) && request != null) {
				ServletContext context = request.getSession().getServletContext();
				path = "WEB-INF/static/links/icon-154.png";
				is = context.getResourceAsStream(path);
			} else {
				path = logoPath + path;
				File file = new File(path);
				is = new FileInputStream(file);
			}
		} catch (Exception e) {
			log.error("getMiddelImg error,Exception -> {}", e);
		}
		return is;
	}
	
	private String qrOssPath(int state,String name) {
		String base = "";
		if (state == 0) {
			base = ossTest;
		} else {
			base = ossOnline;
		}
		base += "qrcode/"+name+".png";
		return base;
	}
	
	public void noticeLogicServer(QRCode qr,String domain) throws Exception {
		String qrLink = domain.substring(0,domain.indexOf("/",8)) + IConsts.LOCALDOWN + qr.getLocalPath().substring(localPath.length());
		//刷新到逻辑服
		mqMgr.sendQrLinkToLogic(qr.getGameId(),qrLink,qr.getLink());
	}
}
