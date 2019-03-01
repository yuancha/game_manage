package com.llmj.oss.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.llmj.oss.alioss.AliOssManager;
import com.llmj.oss.config.GlobalBean;
import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.DownDao;
import com.llmj.oss.dao.UploadDao;
import com.llmj.oss.model.DownLink;
import com.llmj.oss.model.FileOperation;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.model.UploadFile;
import com.llmj.oss.util.FileUtil;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j(topic = "ossLogger")
@RequestMapping("/oss")
public class OssController {
	
	@Autowired
	private AliOssManager ossMgr;
	@Autowired
	private GlobalBean global;
	@Autowired
	private UploadDao uploadDao;
	@Autowired
	private DownDao downDao;
	
	//本地存放路径
	@Value("${upload.local.basePath}")
	private String localPath;
	
	@GetMapping("/fileManage")
	public String fileManage(Model model,HttpServletRequest request) {
		try {
			int state = 0;
			//flag 为 test online
			String gameState = request.getParameter("state");
			if (gameState.equals("online")) {
				state = 1;
			}
			model.addAttribute("gameState", state);
			return "fileManage";
		} catch (Exception e) {
			model.addAttribute("message", e.toString());
			log.error("fileManage error,Exception -> {}",e);
		}
		return "error";
	}
	
	private String getTableName (int state) {
		if (state == 0) {
			return IConsts.UpFileTable.test.getTableName();
		}
		return IConsts.UpFileTable.online.getTableName();
	}
	
	//显示列表
	@PostMapping("/getFilesInfo")
	@ResponseBody
	public RespEntity getFilesInfo(@RequestBody FileOperation param) {
		List<UploadFile> data = null;
		RespEntity resp = new RespEntity();
		try {
			int gameId = param.getGameId();
			int type = param.getGameType();
			int state = param.getGameState();
			log.debug("getFilesInfo param,gameId:{},type:{},state:{}",gameId,type,state);
			
			String packName = global.getPackName(String.valueOf(gameId));
			if (StringUtil.isEmpty(packName)) {
				return new RespEntity(RespCode.GAME_PACKAGE);
			}
			String tableName = getTableName(state);
			
			List<UploadFile> online = uploadDao.selectOnline(tableName,packName, IConsts.UpFileState.online.getState(), type);
			if (online.size() > 1) {
				log.error("线上版本数据出错，size : {},packName:{},type:{}",online.size(),packName,type);
				return new RespEntity(-2,"线上版本数据出错");
			}
			if (!online.isEmpty()) {
				resp.setOnline(online.get(0));
			}
			data = uploadDao.getFiles(tableName,packName, IConsts.UpFileState.delete.getState(), type);
			resp.setData(data);
		} catch (Exception e) {
			log.error("getFilesInfo error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return resp;
	}
	
	//删除文件
	@PostMapping("/delFile")
	@ResponseBody
	public RespEntity delFile(@RequestBody FileOperation param) {
		try {
			int id = param.getId();
			int state = param.getGameState();
			String tableName = getTableName(state);
			UploadFile file = uploadDao.selectById(id,tableName);
			if (file == null || file.getState() == IConsts.UpFileState.delete.getState()) {
				return new RespEntity(-2,"文件不存在");
			}
			if (file.getState() == IConsts.UpFileState.online.getState()) return new RespEntity(-2,"线上版本不允许删除");
			
			//删除oss对应文件 plist
			String ossPath = file.getOssPath();
			ossMgr.removeFile(ossPath);
			if (file.getType() == IConsts.UpFileType.Ios.getType()) {
				//删除ios对应plist文件
				ossMgr.removeFile(ossPath + ".plist");
				file.setOssPath("");
			}
			file.setState(IConsts.UpFileState.delete.getState());
			uploadDao.delFile(tableName,file);
			log.info("delFile success,id:{},packName:{},type:{},state:{}",file.getId(),file.getPackName(),file.getType(),state);
		} catch (Exception e) {
			log.error("delFile error,Exception - > {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	//一键刷包
	@PostMapping("/refreshPack")
	@ResponseBody
	public RespEntity refreshPackage(@RequestBody FileOperation param) {
		try {
			int id = param.getId();
			int state = param.getGameState();
			String tableName = getTableName(state);
			UploadFile file = uploadDao.selectById(id,tableName);
			if (file == null) {
				log.error("文件不存在,id : {},tableName : {}",id,tableName);
				return new RespEntity(-2, "文件不存在");
			}
			if (file.getState() == IConsts.UpFileState.online.getState())
				return new RespEntity(-2, "已是线上版本");
			
			String ossPath = file.getOssPath();
			if (!ossMgr.fileIsExist(ossPath)) {
				log.error("oss上不存在该文件, ossPath : {}",ossPath);
				return new RespEntity(-2, "oss上不存在该文件");
			}
			
			String packName = file.getPackName();
			int type = file.getType();
			
			//ios版本 需要操作plist
			String link = ossPath;
			if (type == IConsts.UpFileType.Ios.getType()) {
				//String[] tmp = file.getLocalPath().split("/");
				String gmPy = file.getPackName().split("\\.")[1];
				String plistPath = localPath + packName + "/" + gmPy + ".plist";// plist模板路径
				String plistStr = FileUtil.fileToString(plistPath,"utf-8");
				if (StringUtil.isEmpty(plistStr)) {
					return new RespEntity(-2,"plist 读取错误");
				}
				ossMgr.changePlist(plistStr,file.getLocalPath()+".plist",ossPath,ossPath+".plist",file);
				ossPath = ossPath+".plist";
			}
			
			//保存到mysql
			String gid = global.getGameIdByPack(packName);
			String dlid = gid + "_" + state + "_" + file.getType();
			DownLink dl = new DownLink();
			dl.setId(dlid);
			dl.setType(file.getType());
			dl.setLink(link);
			downDao.saveLink(dl);
			
			file.setState(IConsts.UpFileState.online.getState());
			uploadDao.updateState(tableName,IConsts.UpFileState.online.getState(), IConsts.UpFileState.up2oss.getState(),file);//先更改之前线上状态信息
			uploadDao.updateState1(tableName,file);	//再修改自己状态信息
			log.info("refreshPack success,packfile info : {}",StringUtil.objToJson(file));
		} catch (Exception e) {
			log.error("refreshPackage error,Exception ->{}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	//复制
	@PostMapping("/copyFile")
	@ResponseBody
	public RespEntity copyFile(@RequestBody FileOperation param) {
		
		int id = param.getId();
		int state = param.getGameState();
		String tableName = getTableName(state);
		try {
			UploadFile file = uploadDao.selectById(id,tableName);
			if (file.getState() == IConsts.UpFileState.delete.getState()) return new RespEntity(RespCode.SERVER_ERROR);
			//获得oss path
			String oldOssPath = file.getOssPath();
			//替换path
			String newOssPath ="";
			if (oldOssPath.indexOf("/test/") > 0) {
				newOssPath = oldOssPath.replace("/test/", "/online/");
			} else if(oldOssPath.indexOf("/online/") > 0) {
				newOssPath = oldOssPath.replace("/online/", "/test/");
			} else {
				return new RespEntity(-2,"初始路径错误，path ："+oldOssPath);
			}
			//oss 检查文件是否存在
			if (ossMgr.fileIsExist(newOssPath)) {
				return new RespEntity(-2,"文件已存在，path:"+newOssPath);
			}
			//oss 复制操作
			ossMgr.copyFile(oldOssPath, newOssPath);
			String targetTable = getTableName(Math.abs(state - 1));
			//insert file
			file.setOssPath(newOssPath);
			file.setState(IConsts.UpFileState.up2oss.getState());
			uploadDao.copyFile(file, targetTable);
			log.info("copy file success,packageName:{},sourcePath:{},targetPath:{}",file.getPackName(),oldOssPath,newOssPath);
		} catch (Exception e) {
			log.error("copyFile error,Exception - > {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		
		return new RespEntity(RespCode.SUCCESS);
	}
}
