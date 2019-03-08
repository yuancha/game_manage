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

import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.DomainDao;
import com.llmj.oss.dao.QrcodeDao;
import com.llmj.oss.manager.AliOssManager;
import com.llmj.oss.manager.MqManager;
import com.llmj.oss.model.QRCode;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.model.oper.QrOperation;
import com.llmj.oss.util.QRCodeUtil;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

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
	
	@Value("${upload.oss.test}")
    public String ossTest;
    @Value("${upload.oss.online}")
    public String ossOnline;
	
	@Autowired
	private QrcodeDao qrDao;
	@Autowired
	private DomainDao domainDao;
	@Autowired
	private AliOssManager ossMgr;
	@Autowired
	private MqManager mqMgr;
	
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
			res.setData(list);
		} catch (Exception e) {
			log.error("qrList error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return res;
	}
	
	@PostMapping("/add")
	@ResponseBody
	public RespEntity qrCodeAdd(@RequestBody QrOperation model) {
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
			String link = saveQr(qr,domain);
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
	
	private String saveQr(QRCode qr,String domain) throws Exception {
		int gameId = qr.getGameId();
		int state = qr.getState();
		String link = domain + "?gameId="+gameId+"&gameState="+state;
		
		if (qrDao.selectByLink(link) != null) {
			return link;
		}
		
		qr.setLink(link);
		byte[] tmp = QRCodeUtil.encode(link);
		String arryStr = Arrays.toString(tmp);
		arryStr = arryStr.substring(1, arryStr.length() - 1);
		qr.setPhoto(arryStr);
		
		String qrSavePath = qrOssPath(state,StringUtil.getUUIDStr());
		ossMgr.uploadFileByByte(qrSavePath, tmp);
		qr.setOssPath(qrSavePath);
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
			int gameId = model.getGameId();
			String link = model.getDomain();
			QRCode old = qrDao.selectByLogicUse(gameId,lUse);
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
			String ossqrLink = ossMgr.ossDomain() + qr.getOssPath();
			//TDOO 刷新到逻辑服
			mqMgr.sendQrLinkToLogic(gameId,ossqrLink);
		} catch (Exception e) {
			log.error("qrCodeRefresh error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return res;
	}
	
	//逻辑服主动请求二维码
	@PostMapping("/getIconLink")
	@ResponseBody
	public RespEntity qrCodeRefresh(HttpServletRequest request) {
		RespEntity res = new RespEntity();
		try {
			String gameId = request.getParameter("gameId");
			String state = request.getParameter("state");
			System.out.println(gameId+"----"+state);
			res.setData("http://qrcode.icon.link");
		} catch (Exception e) {
			log.error("qrCodeRefresh error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return res;
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
}