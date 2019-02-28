package com.llmj.oss.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.llmj.oss.alioss.AliOssManager;
import com.llmj.oss.config.GlobalBean;
import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.UploadDao;
import com.llmj.oss.model.FileOperation;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.model.UploadFile;
import com.llmj.oss.util.FileUtil;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j(topic = "ossLogger")
@RequestMapping("/oss")
public class OperationController {
	
	@Autowired
	private AliOssManager ossMgr;
	@Autowired
	private GlobalBean global;
	@Autowired
	private UploadDao uploadDao;
	
	//本地存放路径
	@Value("${upload.local.basePath}")
	private String localPath;
	
	@GetMapping("/fileManage")
	public String fileManage() {
		return "test";
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
			data = uploadDao.getFiles(tableName,packName, IConsts.UpFileState.delete.getState(), type);
		} catch (Exception e) {
			log.error("getFilesInfo error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS,data);
	}
	
	//删除文件
	@PostMapping("/delFile")
	@ResponseBody
	public RespEntity delFile(@RequestBody FileOperation param) {
		int id = param.getId();
		int state = param.getGameState();
		String tableName = getTableName(state);
		UploadFile file = uploadDao.selectById(id,tableName);
		if (file == null || file.getState() == IConsts.UpFileState.delete.getState()) {
			return new RespEntity(-2,"文件不存在");
		}
		if (file.getState() == IConsts.UpFileState.online.getState()) return new RespEntity(-2,"线上版本不允许删除");
		
		if (file.getState() == IConsts.UpFileState.up2oss.getState()) {
			//TODO 删除oss对应文件 plist
			file.setOssPath("");
		}
		file.setState(IConsts.UpFileState.delete.getState());
		uploadDao.delFile(tableName,file);
		log.info("delFile success,id:{},packName:{},type:{},state:{}",file.getId(),file.getPackName(),file.getType(),state);
		return new RespEntity(RespCode.SUCCESS);
	}
	
	//一键刷包
	@PostMapping("/refreshPack")
	@ResponseBody
	public RespEntity refreshPackage(@RequestBody FileOperation param) {
		int id = param.getId();
		int state = param.getGameState();
		String tableName = getTableName(state);
		UploadFile file = uploadDao.selectById(id,tableName);
		if (file == null) {
			return new RespEntity(-2, "文件不存在");
		}
		if (file.getState() == IConsts.UpFileState.online.getState())
			return new RespEntity(-2, "已是线上版本");
		
		String packName = file.getPackName();
		int type = file.getType();
		String ossPath = "";
		if (type == IConsts.UpFileType.Android.getType()) {
			//ossPath = basePath + packName + "/" + IConsts.UpFileType.Android.getDesc() + "/";
		} else {
			//ossPath = basePath + packName + "/" + IConsts.UpFileType.Ios.getDesc() + "/";
		}
		
		//找本地html 并修改 文件
		String tmpletPath = localPath + packName + "/";//html plist模板路径
		String[] tmp = file.getLocalPath().split("/");
		String tmpletName = file.getPackName().split("\\.")[1];
		if (type == IConsts.UpFileType.Android.getType()) {
			String htmlPath = tmpletPath + tmpletName + ".html";
			String htmlStr = FileUtil.fileToString(htmlPath,"utf-8");
			if (StringUtil.isEmpty(htmlStr)) {
				return new RespEntity(-2,"html 读取错误");
			}
			//修改html 不上传
			//ossMgr.changeHtml(htmlStr,ossPath+tmp[tmp.length - 1],ossPath+tmpletName+".html");
		} else {
			//找本地plist并修改 文件
			String plistPath = tmpletPath + tmpletName+".plist";
			String plistStr = FileUtil.fileToString(plistPath,"utf-8");
			String htmlPath = tmpletPath + tmpletName + ".html";
			String htmlStr = FileUtil.fileToString(htmlPath,"utf-8");
			if (StringUtil.isEmpty(plistStr) || StringUtil.isEmpty(htmlStr)) {
				return new RespEntity(-2,"plist 或 html 读取错误");
			}
			//ossMgr.changePlist(plistStr,file.getLocalPath()+".plist",ossPath+tmp[tmp.length - 1],ossPath+tmp[tmp.length - 1]+".plist",file);
			//ossMgr.changeHtml(htmlStr,ossPath+tmp[tmp.length - 1]+".plist",ossPath+tmpletName+".html");
		}
		
		// 上传包到指定路径
		/*boolean success = ossMgr.uploadFile(ossPath+tmp[tmp.length - 1], file.getLocalPath());
		if (!success) {
			return new RespEntity(-2,"上传错误");
		}*/
		file.setState(IConsts.UpFileState.online.getState());
		file.setOssPath(ossPath+tmp[tmp.length - 1]);
		uploadDao.updateState(tableName,IConsts.UpFileState.online.getState(), IConsts.UpFileState.up2oss.getState(),file);//先更改之前线上状态信息
		uploadDao.upToOss(tableName,file);
		log.info("refreshPack success,packfile info : {}",StringUtil.objToJson(file));
		return new RespEntity(RespCode.SUCCESS);
	}
	
	//复制
	@PostMapping("/copyFile")
	@ResponseBody
	public RespEntity copyFile(@RequestBody FileOperation param) {
		
		int id = param.getId();
		int state = param.getGameState();
		String tableName = getTableName(state);
		UploadFile file = uploadDao.selectById(id,"");
		//获得oss path
		//替换path
		//oss 检查文件是否存在
		//oss 复制操作
		String targetTable = getTableName(Math.abs(state - 1));
		//insert file
		
		return new RespEntity(RespCode.SUCCESS);
	}
}
