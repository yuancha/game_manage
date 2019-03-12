package com.llmj.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.DomainDao;
import com.llmj.oss.dao.QrcodeDao;
import com.llmj.oss.manager.AliOssManager;
import com.llmj.oss.manager.MqManager;
import com.llmj.oss.manager.SwitchManager;
import com.llmj.oss.model.QRCode;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.model.oper.QrOperation;
import com.llmj.oss.util.FileUtil;
import com.llmj.oss.util.QRCodeUtil;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * 二维码管理
 * @author xinghehudong
 *
 */
@Controller
@Slf4j(topic = "ossLogger")
@RequestMapping("/qrCode")
public class QRCodeController {
	
	private static final int lUse = 1;	//逻辑服使用标识
	private static final int lNoUse = 0;
	//sate 0表示测试服 1表示正式线上
	
	@Value("${upload.oss.test}")
    public String ossTest;
    @Value("${upload.oss.online}")
    public String ossOnline;
    @Value("${upload.local.qrcode}")
    public String qrcodePath;
	
	@Autowired
	private QrcodeDao qrDao;
	@Autowired
	private DomainDao domainDao;
	@Autowired
	private AliOssManager ossMgr;
	@Autowired
	private MqManager mqMgr;
	@Autowired 
	private SwitchManager switchMgr;
	
	@GetMapping("")
	public String qrCodeHome(Model model,HttpServletRequest request) {
		try {
			int state = 0;
			//flag 为 test online
			String gameState = request.getParameter("state");
			if (gameState.equals("online")) {
				state = 1;
			}
			model.addAttribute("gameState", state);
			return "qrcodeManage";
		} catch (Exception e) {
			model.addAttribute("message", "server error");
			log.error("packHome error,Exception -> {}",e);
		}
		return "error";
	}
	
	@PostMapping("/list")
	@ResponseBody
	public RespEntity qrCodeList(@RequestBody QrOperation model) {
		RespEntity res = new RespEntity();
		try {
			int gameId = model.getGameId();
			int state = model.getState();
			List<QRCode> list = qrDao.getQRs(gameId,state);
			for (QRCode qr : list) {
				switchMgr.getQrcodeLink(qr);
			}
			res.setData(list);
		} catch (Exception e) {
			log.error("qrList error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return res;
	}
	
	@PostMapping("/add")
	@ResponseBody
	public RespEntity qrCodeAdd(@RequestBody QrOperation model,HttpServletRequest request) {
		try {
			int gameId = model.getGameId();
			int state = model.getState();
			QRCode qr = new QRCode();
			qr.setContent(model.getDesc());
			qr.setGameId(gameId);
			qr.setState(state);
			//域名拼接成链接
			String domain = model.getDomain();
			if (domainDao.selectById(domain) == null) {
				log.error("无效的域名，domain : {}",domain);
				return new RespEntity(-2,"无效域名");
			}
			InputStream is = getMiddelImg(gameId,request);
			String link = saveQr(qr,domain,is);
			if (!link.equals("")) {
				log.error("链接已存在，link ： {}",link);
				return new RespEntity(-2,"链接已存在,"+link);
			}
			qrDao.save(qr);
			log.info("二维码保存成功，info - > {}",StringUtil.objToJson(qr));
		} catch (Exception e) {
			log.error("qrCodeAdd error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	/*@PostMapping("/update")
	@ResponseBody
	public RespEntity qrCodeUpdate(@RequestBody QrOperation model) {
		try {
			QRCode qr = qrDao.selectById(model.getId());
			if (qr == null) {
				log.error("QRCode not find,id -> {}",model.getId());
				return new RespEntity(-2,"数据不存在");
			}
			String domain = model.getDomain();
			if (domainDao.selectById(domain) == null) {
				log.error("无效的域名，domain : {}",domain);
				return new RespEntity(-2,"无效域名");
			}
			qr.setContent(model.getDesc());
			String link = saveQr(qr,domain);
			if (!link.equals("")) {
				log.error("链接已存在，link ： {}",link);
				return new RespEntity(-2,"链接已存在,"+link);
			}
			qrDao.updateQr(qr);
			log.info("二维码更新成功，info - > {}",StringUtil.objToJson(qr));
		} catch (Exception e) {
			log.error("qrCodeUpdate error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}*/
	
	private String saveQr(QRCode qr,String domain,InputStream is) throws Exception {
		int gameId = qr.getGameId();
		int state = qr.getState();
		String link = domain + "?gameId="+gameId+"&gameState="+state;
		
		if (qrDao.selectByLink(link) != null) {
			return link;
		}
		
		try {
			qr.setLink(link);
			byte[] tmp = QRCodeUtil.encode(link,is);
			/*String arryStr = Arrays.toString(tmp);
			arryStr = arryStr.substring(1, arryStr.length() - 1);*/
			qr.setPhoto("");
			//qr.setPhoto(arryStr);
			
			//保存到oss
			String qrName = StringUtil.getUUIDStr();
			String qrSavePath = qrOssPath(state,qrName);
			ossMgr.uploadFileByByte(qrSavePath, tmp);
			qr.setOssPath(qrSavePath);
			
			//保存到本地
			is.reset();//重复利用
			QRCodeUtil.encode(link,qrcodePath,qrName,is);
			qr.setLocalPath(qrcodePath + qrName + ".png");
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return "";
	}
	
	@PostMapping("/del")
	@ResponseBody
	public RespEntity qrCodeDel(@RequestBody QrOperation model) {
		try {
			String link = model.getDomain();
			QRCode qr = qrDao.selectByLink(link);
			if (qr == null) {
				return new RespEntity(RespCode.SUCCESS);
			}
			if (qr.getLogicUse() == lUse) {
				return new RespEntity(-2,"已在逻辑服备份，请先刷新其它二维码备份后再删除");
			}
			qrDao.delQR(model.getDomain());
			ossMgr.removeFile(qr.getOssPath());
			FileUtil.deleteFile(qr.getLocalPath());
			log.info("二维码删除成功，link : {}",model.getDomain());
		} catch (Exception e) {
			log.error("qrCodeDel error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	@PostMapping("/domain")
	@ResponseBody
	public RespEntity qrCodeDomain() {
		RespEntity res = new RespEntity();
		try {
			res.setData(domainDao.selectByType(0));
		} catch (Exception e) {
			log.error("qrCodeDomain error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return res;
	}
	
	/**
	 * 刷新通知逻辑服
	 * @return
	 */
	@PostMapping("/refresh")
	@ResponseBody
	public RespEntity qrCodeRefresh(@RequestBody QrOperation model) {
		RespEntity res = new RespEntity();
		try {
			int state = model.getState();
			if (state == 0) {
				return new RespEntity(-2,"测试无需备份逻辑服");
			}
			
			int gameId = model.getGameId();
			String link = model.getDomain();
			QRCode old = qrDao.selectByLogicUse(gameId,lUse,state);
			QRCode qr = qrDao.selectByLink(link);
			if (qr == null) {
				log.error("qrcode not find,link ： {}",link);
				return new RespEntity(-2,"数据错误");
			}
			if (old != null) {
				if (old.getLink().equals(link)) {
					return new RespEntity(-2,"已经是备份状态");
				}
				old.setLogicUse(lNoUse);
				qrDao.updateLogicUse(old);
			}
			qr.setLogicUse(lUse);
			qrDao.updateLogicUse(qr);
			String qrLink = switchMgr.getQrcodeLink(qr);;
			//刷新到逻辑服
			mqMgr.sendQrLinkToLogic(gameId,qrLink);
		} catch (Exception e) {
			log.error("qrCodeRefresh error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		res.setMessage("备份逻辑服成功");
		return res;
	}
	
	//逻辑服主动请求二维码
	@RequestMapping("/getIconLink")
	@ResponseBody
	public RespEntity qrCodeRefresh(HttpServletRequest request) {
		RespEntity res = new RespEntity();
		try {
			String gameId = request.getParameter("gameId");
			log.debug("getIconLink recive param, gameId : {}" , gameId);
			QRCode qr = qrDao.selectByLogicUse(Integer.parseInt(gameId),lUse,1);
			if (qr == null) {
				return new RespEntity(-2,"无可用二维码");
			}
			String link = switchMgr.getQrcodeLink(qr);
			res.setData(link);
		} catch (Exception e) {
			log.error("qrCodeRefresh error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return res;
	}
	
	@GetMapping("/icons")
	public String onlineIcons(Model model,HttpServletRequest request) {
		try {
			List<QRCode> list = qrDao.getOnlineQRs(lUse, 1);
			for (QRCode qr : list) {
				switchMgr.getQrcodeLink(qr);
			}
			model.addAttribute("qrs", StringUtil.objToJson(list));
			return "qrOnlineIcons";
		} catch (Exception e) {
			log.error("onlineIcons error,Exception -> {}",e);
			model.addAttribute("message", "server error");
		}
		return "error";
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
	
	//获得二维码中间图片
	private InputStream getMiddelImg(int gameId,HttpServletRequest request) {
		InputStream is = null;
		try {
			ServletContext context = request.getSession().getServletContext();
			String path = "WEB-INF/static/images/";
			switch (gameId) {
				default : {
					path += "test.png";
					break;
				}
			}
			is = context.getResourceAsStream(path);
		} catch (Exception e) {
			log.error("getMiddelImg error,Exception -> {}",e);
		}
		return is;
	}
}