package com.llmj.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.PackageDao;
import com.llmj.oss.manager.OpLogManager;
import com.llmj.oss.manager.PackManager;
import com.llmj.oss.model.PackageName;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.model.oper.PackOperation;
import com.llmj.oss.util.StringUtil;

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
	@Autowired
	private OpLogManager logMgr;
	
	@GetMapping("")
	public String packHome(Model model,HttpServletRequest request) {
		
		return "packName";
	}
	
	@PostMapping("/list")
	@ResponseBody
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
	@ResponseBody
	public RespEntity packAdd(@RequestBody PackOperation model,HttpServletRequest request) {
		try {
			PackageName pn = objChange(model);
			String gameName = pn.getContent();
			if (StringUtil.isEmpty(gameName)) {
				return new RespEntity(-1, "游戏名称不能为空");
			}
			PackageName tmp = packDao.selectByGameName(gameName);
			if (tmp != null) {
				return new RespEntity(-1, "游戏名称已存在");
			}
			packDao.savePack(pn);
			log.info("pack add success,info : {}",StringUtil.objToJson(pn));
			
			String account = (String) request.getSession().getAttribute("account");
			StringBuilder sb = new StringBuilder("包名新增，内容：");
			sb.append(StringUtil.objToJson(pn));
			logMgr.opLogSave(account,OpLogManager.pack_log,sb.toString());
		} catch (Exception e) {
			log.error("packAdd error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	@PostMapping("/update")
	@ResponseBody
	public RespEntity packUpdate(@RequestBody PackOperation model,HttpServletRequest request) {
		try {
			PackageName pn = objChange(model);
			PackageName old = packDao.selectById(pn.getGameId());
			if (old == null) {
				return new RespEntity(-2,"数据错误");
			}
			packDao.updatePack(pn);
			log.info("pack update success,info : {}",StringUtil.objToJson(pn));
			
			String account = (String) request.getSession().getAttribute("account");
			StringBuilder sb = new StringBuilder("包名修改，旧内容：");
			sb.append(StringUtil.objToJson(old));
			sb.append(",新内容：");
			sb.append(StringUtil.objToJson(pn));
			logMgr.opLogSave(account,OpLogManager.pack_log,sb.toString());
		} catch (Exception e) {
			log.error("packUpdate error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	@PostMapping("/del")
	@ResponseBody
	public RespEntity packDel(@RequestBody PackOperation model,HttpServletRequest request) {
		try {
			PackageName old = packDao.selectById(model.getGameId());
			if (old == null) {
				return new RespEntity(-2,"数据错误");
			}
			packDao.delPack(model.getGameId());
			log.info("pack del success,gameId : {}",model.getGameId());
			
			String account = (String) request.getSession().getAttribute("account");
			StringBuilder sb = new StringBuilder("包名删除，内容：");
			sb.append(StringUtil.objToJson(old));
			logMgr.opLogSave(account,OpLogManager.pack_log,sb.toString());
		} catch (Exception e) {
			log.error("packDel error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	private PackageName objChange(PackOperation model) {
		PackageName pn = new PackageName();
		pn.setGameId(model.getGameId());
		pn.setContent(model.getContent());
		pn.setAndroid(model.getAndroid());
		pn.setIos(model.getIos());
		return pn;
	}
	
	@PostMapping("/get")
	@ResponseBody
	public RespEntity packGet(@RequestBody PackOperation model) {
		RespEntity res = new RespEntity(RespCode.SUCCESS);
		try {
			int gameId = model.getGameId();
			PackageName pn = packDao.selectById(gameId);
			res.setData(pn);
		} catch (Exception e) {
			log.error("packAdd error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return res;
	}
}