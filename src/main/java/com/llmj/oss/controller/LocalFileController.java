package com.llmj.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.RedisConsts;
import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.UploadDao;
import com.llmj.oss.manager.AliOssManager;
import com.llmj.oss.manager.SwitchManager;
import com.llmj.oss.model.LocalFile;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.model.UploadFile;
import com.llmj.oss.model.oper.LocalFileOper;
import com.llmj.oss.model.oper.PackOperation;
import com.llmj.oss.util.FileUtil;
import com.llmj.oss.util.RedisTem;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 包名管理
 * @author xinghehudong
 *
 */
@Controller
@Slf4j(topic = "ossLogger")
@RequestMapping("/lofile")
public class LocalFileController {
	
	@Value("${upload.local.basePath}")
	private String localPath;
	
	@Autowired
	private UploadDao uploadDao;
	@Autowired 
	private SwitchManager switchMgr;
	@Autowired 
	private RedisTem redis;
	@Autowired
	private AliOssManager ossMgr;
	
	@GetMapping("")
	public String fileHome(Model model,HttpServletRequest request) {
		
		return "localfile";
	}
	
	@PostMapping("/count")
	@ResponseBody
	public RespEntity fileCount(@RequestBody LocalFileOper model) {
		RespEntity res = new RespEntity();
		try {
			int gameId = model.getGameId();
			int mum = redis.llenPrefix(RedisConsts.PRE_FILE_KEY+gameId, RedisConsts.FILE_LIST_KEY);
			res.setData(mum);
		} catch (Exception e) {
			log.error("fileCount error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return res;
	}
	
	@PostMapping("/list")
	@ResponseBody
	public RespEntity fileList(@RequestBody LocalFileOper model) {
		RespEntity res = new RespEntity();
		try {
			int gameId = model.getGameId();
			int page = model.getPage();
			int end = page * 10 - 1;
			int start = end - 9;
			List<LocalFile> list = new ArrayList<>();
			String prekey = RedisConsts.PRE_FILE_KEY+gameId;
			List<String> filenames = redis.lrangePre(prekey, RedisConsts.FILE_LIST_KEY, start, end);
			for (String tmp : filenames) {
				LocalFile file = new LocalFile();
				file.setFileName(tmp);
				String testId = redis.hgetPrefix(prekey, RedisConsts.FILE_Map_KEY+"test", tmp);
				UploadFile test = null;
				if (!StringUtil.isEmpty(testId)) {
					test = uploadDao.selectById(Integer.parseInt(testId), IConsts.UpFileTable.test.getTableName());
				}
				UploadFile online = null;
				String onlineId = redis.hgetPrefix(prekey, RedisConsts.FILE_Map_KEY+"online", tmp);
				if (!StringUtil.isEmpty(onlineId)) {
					online = uploadDao.selectById(Integer.parseInt(onlineId), IConsts.UpFileTable.online.getTableName());
				}
				if (test == null && online == null) {
					redis.lremWithPrefix(prekey, RedisConsts.FILE_LIST_KEY, 1, tmp);
					continue;
				}
				if (test != null) {
					file.setUpName(test.getFileName());
					file.setTestState(test.getState());
				} else {
					file.setTestState(-1);
				}
				if (online != null) {
					file.setUpName(online.getFileName());
					file.setOnlineState(online.getState());
				} else {
					file.setOnlineState(-1);
				}
				list.add(file);
			}
			res.setData(list);
		} catch (Exception e) {
			log.error("fileList error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return res;
	}
	
	@PostMapping("/del")
	@ResponseBody
	public RespEntity fileDel(@RequestBody LocalFileOper model) {
		try {
			int gameId = model.getGameId();
			String filename = model.getFileName();
			String prekey = RedisConsts.PRE_FILE_KEY+gameId;
			String testId = redis.hgetPrefix(prekey, RedisConsts.FILE_Map_KEY+"test", filename);
			UploadFile test = null;
			if (!StringUtil.isEmpty(testId)) {
				test = uploadDao.selectById(Integer.parseInt(testId), IConsts.UpFileTable.test.getTableName());
				if (test.getState() == IConsts.UpFileState.online.getState()) {
					return new RespEntity(-2,"线上版本不可删除");
				}
			}
			String onlineId = redis.hgetPrefix(prekey, RedisConsts.FILE_Map_KEY+"online", filename);
			UploadFile online = null;
			if (!StringUtil.isEmpty(onlineId)) {
				online = uploadDao.selectById(Integer.parseInt(onlineId), IConsts.UpFileTable.online.getTableName());
				if (online.getState() == IConsts.UpFileState.online.getState()) {
					return new RespEntity(-2,"线上版本不可删除");
				}
			}
			redis.lremWithPrefix(prekey, RedisConsts.FILE_LIST_KEY, 1, filename);
			redis.hdelPrefix(prekey, RedisConsts.FILE_Map_KEY+"test", filename);
			redis.hdelPrefix(prekey, RedisConsts.FILE_Map_KEY+"online", filename);
			String path = "";
			if (test != null) {
				path = test.getLocalPath();
				uploadDao.deleteFile(test.getId(),IConsts.UpFileTable.test.getTableName());
				if (!StringUtil.isEmpty(test.getOssPath())) {
					ossMgr.removeFile(test.getOssPath(),gameId);
					if (test.getType() == IConsts.UpFileType.Ios.getType()) {
						//删除ios对应plist文件
						ossMgr.removeFile(test.getOssPath() + ".plist",gameId);
					}
				}
			}
			if (online != null) {
				path = online.getLocalPath();
				uploadDao.deleteFile(online.getId(),IConsts.UpFileTable.online.getTableName());
				if (!StringUtil.isEmpty(online.getOssPath())) {
					ossMgr.removeFile(online.getOssPath(),gameId);
					if (online.getType() == IConsts.UpFileType.Ios.getType()) {
						//删除ios对应plist文件
						ossMgr.removeFile(online.getOssPath() + ".plist",gameId);
					}
				}
			}
			
			//删除文件所在文件夹下文件
			FileUtil.deleteFileParentsDir(path);
			log.info("彻底删除文件成功，文件名: {},gameId:{}",filename,gameId);
		} catch (Exception e) {
			log.error("fileDel error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	//下载
	@PostMapping("/down")
	public void fileDown(HttpServletResponse response,
			@RequestParam(name = "gameId") int gameId, @RequestParam(name = "fileName") String filename) {
		try {
    		String path = null;
    		String prekey = RedisConsts.PRE_FILE_KEY+gameId;
    		String testId = redis.hgetPrefix(prekey, RedisConsts.FILE_Map_KEY+"test", filename);
			if (!StringUtil.isEmpty(testId)) {
				UploadFile test = uploadDao.selectById(Integer.parseInt(testId), IConsts.UpFileTable.test.getTableName());
				path = test.getLocalPath();
			}
			if (path == null) {
				String onlineId = redis.hgetPrefix(prekey, RedisConsts.FILE_Map_KEY+"online", filename);
				if (!StringUtil.isEmpty(onlineId)) {
					UploadFile online = uploadDao.selectById(Integer.parseInt(onlineId), IConsts.UpFileTable.online.getTableName());
					path = online.getLocalPath();
				}
			}
    		
        	if (path == null) {
        		log.error("fileDown not find,filename -> {}",filename);
        		return;
        	}
        	String domain = switchMgr.getUseDomain(gameId,0);
    		if (StringUtil.isEmpty(domain)) {
    			log.error("domain 获取错误，数据为空");
    			return;
    		}
        	response.sendRedirect(domain + IConsts.LOCALDOWN + path.substring(localPath.length()));
		} catch (Exception e) {
			log.error("fileDown error, Exception -> {}",e);
		}
	}
	
	//上传到oss
	@PostMapping("/uptoOss")
	@ResponseBody
	public RespEntity fileToOss(@RequestBody PackOperation model) {
		try {
			
		} catch (Exception e) {
			log.error("fileToOss error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
}