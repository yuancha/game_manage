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
	
	@Autowired
	private QrcodeDao qrDao;
	@Autowired
	private DomainDao domainDao;
	
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
	
	@PostMapping("/update")
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
	}
	
	private String saveQr(QRCode qr,String domain) throws Exception {
		int gameId = qr.getGameId();
		int state = qr.getState();
		String link = domain + "?gameId="+gameId+"&gameState="+state;
		
		if (qrDao.selectByLink(link,qr.getId()) != null) {
			return link;
		}
		
		qr.setLink(link);
		String arryStr = Arrays.toString(QRCodeUtil.encode(link));
		arryStr = arryStr.substring(1, arryStr.length() - 1);
		qr.setPhoto(arryStr);
		
		//TODO 更新二维码相关地方
		return "";
	}
	
	@PostMapping("/del")
	@ResponseBody
	public RespEntity qrCodeDel(@RequestBody QrOperation model) {
		try {
			qrDao.delQR(model.getDomain());
			log.info("二维码删除成功，link : {}",model.getDomain());
			//TODO 更新二维码相关地方
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
	
}