package com.llmj.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.PackageDao;
import com.llmj.oss.manager.PackManager;
import com.llmj.oss.model.PackageName;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.model.oper.PackOperation;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 包名管理
 * @author xinghehudong
 *
 */
@Controller
@Slf4j(topic = "ossLogger")
@RequestMapping("/package")
public class PackageController {
	
	@Autowired
	private PackageDao packDao;
	@Autowired
	private PackManager packMgr;
	
	@GetMapping("/home")
	public String packHome(Model model,HttpServletRequest request) {
		
		return "packhome";
	}
	
	@PostMapping("/list")
	public RespEntity packList() {
		RespEntity res = new RespEntity();
		try {
			List<PackageName> list = packDao.getAll();
			res.setData(list);
		} catch (Exception e) {
			log.error("packList error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return res;
	}
	
	@PostMapping("/add")
	public RespEntity packAdd(PackOperation model) {
		try {
			PackageName pn = objChange(model);
			packDao.savePack(pn);
			packMgr.refreshPack(pn);
		} catch (Exception e) {
			log.error("packAdd error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	@PostMapping("/update")
	public RespEntity packUpdate(PackOperation model) {
		try {
			PackageName pn = objChange(model);
			packDao.updatePack(pn);
			packMgr.refreshPack(pn);
		} catch (Exception e) {
			log.error("packUpdate error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	@PostMapping("/del")
	public RespEntity packDel(PackOperation model) {
		try {
			packDao.delPack(model.getGameId());
			packMgr.delPack(model.getGameId());
		} catch (Exception e) {
			log.error("packDel error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	@PostMapping("/reload")
	public RespEntity packReload() {
		try {
			packMgr.packNameInit();
		} catch (Exception e) {
			log.error("packReload error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	private PackageName objChange(PackOperation model) {
		PackageName pn = new PackageName();
		pn.setGameId(model.getGameId());
		pn.setDesc(model.getDesc());
		pn.setAndroid(model.getAndroid());
		pn.setIos(model.getIos());
		return pn;
	}
}