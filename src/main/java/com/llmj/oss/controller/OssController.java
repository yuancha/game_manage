package com.llmj.oss.controller;

import java.util.ArrayList;
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

import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.RedisConsts;
import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.DownDao;
import com.llmj.oss.dao.GameControlDao;
import com.llmj.oss.dao.OssConnectDao;
import com.llmj.oss.dao.UploadDao;
import com.llmj.oss.manager.AliOssManager;
import com.llmj.oss.manager.OpLogManager;
import com.llmj.oss.manager.PackManager;
import com.llmj.oss.manager.SwitchManager;
import com.llmj.oss.model.DownLink;
import com.llmj.oss.model.GameControl;
import com.llmj.oss.model.OssConnect;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.model.UploadFile;
import com.llmj.oss.model.oper.FileOperation;
import com.llmj.oss.util.FileUtil;
import com.llmj.oss.util.RedisTem;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j(topic = "ossLogger")
@RequestMapping("/oss")
public class OssController {
	
	@Autowired
	private AliOssManager ossMgr;
	@Autowired
	private PackManager packMgr;
	@Autowired
	private UploadDao uploadDao;
	@Autowired
	private DownDao downDao;
	@Autowired 
	private RedisTem redis;
	@Autowired
	private GameControlDao gameDao;
	@Autowired
	private OssConnectDao ossDao;
	@Autowired
	private SwitchManager switchMgr;
	@Autowired
	private OpLogManager logMgr;
	
	//本地存放路径
	@Value("${upload.local.basePath}")
	private String localPath;
	
	@GetMapping("/home")
	public String home(Model model,HttpServletRequest request) {
		return "ossConnect";
	}
	
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
			model.addAttribute("gameOpens", gameDao.selectOpens());
			
			return "all";
		} catch (Exception e) {
			model.addAttribute("message", e.toString());
			log.error("fileManage error,Exception -> {}",e);
		}
		return "error";
	}
	
	public static String getTableName (int state) {
		if (state == 0) {
			return IConsts.UpFileTable.test.getTableName();
		}
		return IConsts.UpFileTable.online.getTableName();
	}
	
	//显示列表
	@PostMapping("/getFilesInfo")
	@ResponseBody
	public RespEntity getFilesInfo(@RequestBody FileOperation param) {
		List<UploadFile> data = new ArrayList<>();
		RespEntity resp = new RespEntity();
		try {
			int gameId = param.getGameId();
			int type = param.getGameType();
			int state = param.getGameState();
			//log.debug("getFilesInfo param,gameId:{},type:{},state:{}",gameId,type,state);
			
			String tableName = getTableName(state);
			List<UploadFile> online = uploadDao.selectOnline(tableName,gameId, IConsts.UpFileState.online.getState(), type);
			if (online.size() > 1) {
				log.error("线上版本数据出错，size : {},gameId:{},type:{}",online.size(),gameId,type);
				return new RespEntity(-2,"线上版本数据出错");
			}
			if (!online.isEmpty()) {
				resp.setOnline(online.get(0));
				data.add(online.get(0));
			}
			data.addAll(uploadDao.getFiles(tableName,gameId, IConsts.UpFileState.delete.getState(), type));
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
	public RespEntity delFile(@RequestBody FileOperation param,HttpServletRequest request) {
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
			int gameId = file.getGameId();
			String ossPath = file.getOssPath();
			if (!StringUtil.isEmpty(ossPath)) {
				ossMgr.removeFile(ossPath,gameId);
				if (file.getType() == IConsts.UpFileType.Ios.getType()) {
					//删除ios对应plist文件
					ossMgr.removeFile(ossPath + ".plist",gameId);
					file.setOssPath("");
				}
			}
			file.setState(IConsts.UpFileState.delete.getState());
			uploadDao.delFile(tableName,file);
			log.info("delFile success,id:{},packName:{},type:{},state:{}",file.getId(),file.getPackName(),file.getType(),state);
			
			if (state == 1) {//正式数据才保存
				String account = (String) request.getSession().getAttribute("account");
				StringBuilder sb = new StringBuilder("app文件删除，游戏id：");
				sb.append(file.getGameId());
				sb.append(",文件名：").append(file.getFileName());
				sb.append(",数据表id：").append(id);
				logMgr.opLogSave(account,OpLogManager.app_log,sb.toString());
			}
		} catch (Exception e) {
			log.error("delFile error,Exception - > {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	//一键刷包
	@PostMapping("/refreshPack")
	@ResponseBody
	public RespEntity refreshPackage(@RequestBody FileOperation param,HttpServletRequest request) {
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
			
			int gameId = file.getGameId();
			int type = file.getType();
			
			String ossPath = file.getOssPath();
			String localPath = file.getLocalPath();
			if (switchMgr.getOssSwitch(gameId)) {//oss 服务正常
				if (StringUtil.isEmpty(ossPath)) {
					return new RespEntity(-2, "oss路径错误");
				}
				if (!ossMgr.fileIsExist(ossPath,gameId)) {
					log.error("oss上不存在该文件, ossPath : {}",ossPath);
					return new RespEntity(-2, "oss上不存在该文件");
				}
			} else {
				if (!FileUtil.fileExist(localPath)) {
					log.error("本地不存在该文件, localPath : {}",localPath);
					return new RespEntity(-2, "本地不存在该文件");
				}
			}
			
			String link = "";
			if (switchMgr.getOssSwitch(gameId)) {//oss 服务正常
				link = ossPath;
				//ios版本 需要操作plist
				if (type == IConsts.UpFileType.Ios.getType()) {
					String plistPath = "config/template.plist"; // plist模板路径
					String plistStr = FileUtil.fileToString(plistPath,"utf-8");
					if (StringUtil.isEmpty(plistStr)) {
						return new RespEntity(-2,"plist 读取错误");
					}
					//oss plist上传
					ossMgr.changePlist(plistStr,ossPath,ossPath+".plist",file,gameId);
					link = ossPath+".plist";
					//本地plist保存
					ossMgr.changeLocalPlist(plistStr,localPath,file);
				}
			} 
			
			//本地始终保存一份
			if (type == IConsts.UpFileType.Ios.getType()) {
				String plistPath = "config/template.plist"; // plist模板路径
				String plistStr = FileUtil.fileToString(plistPath,"utf-8");
				if (StringUtil.isEmpty(plistStr)) {
					return new RespEntity(-2,"plist 读取错误");
				}
				//本地plist保存
				ossMgr.changeLocalPlist(plistStr,localPath,file);
			}
			
			//保存到mysql
			String dlid = gameId + "_" + state + "_" + file.getType();
			DownLink dl = new DownLink();
			dl.setId(dlid);
			dl.setType(file.getType());
			dl.setLink(link);
			dl.setTargetId(id);
			
			downDao.saveLink(dl);
			//删除redis存储连接
			redis.del(RedisConsts.PRE_LINK_KEY, dlid);
			
			file.setState(IConsts.UpFileState.online.getState());
			uploadDao.updateState(tableName,IConsts.UpFileState.online.getState(), IConsts.UpFileState.up2oss.getState(),file);//先更改之前线上状态信息
			uploadDao.updateState1(tableName,file);	//再修改自己状态信息
			log.info("refreshPack success,packfile info : {}",StringUtil.objToJson(file));
			
			if (state == 1) {//正式数据才保存
				String account = (String) request.getSession().getAttribute("account");
				StringBuilder sb = new StringBuilder("app文件上线，游戏id：");
				sb.append(file.getGameId());
				sb.append(",文件名：").append(file.getFileName());
				sb.append(",数据表id：").append(id);
				logMgr.opLogSave(account,OpLogManager.app_log,sb.toString());
			}
		} catch (Exception e) {
			log.error("refreshPackage error,Exception ->{}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	//复制
	@PostMapping("/copyFile")
	@ResponseBody
	public RespEntity copyFile(@RequestBody FileOperation param,HttpServletRequest request) {
		
		int id = param.getId();
		int state = param.getGameState();
		String tableName = getTableName(state);
		try {
			UploadFile file = uploadDao.selectById(id,tableName);
			if (file.getState() == IConsts.UpFileState.delete.getState()) return new RespEntity(RespCode.SERVER_ERROR);
			int gameId = file.getGameId();

			//oss 复制操作
			String targetTable = getTableName(Math.abs(state - 1));
			if (switchMgr.getOssSwitch(gameId)) {//oss 服务正常
				//获得oss path
				String oldOssPath = file.getOssPath();
				//替换path
				String newOssPath ="";
				if (oldOssPath.indexOf("/test/") > -1) {
					newOssPath = oldOssPath.replace("/test/", "/online/");
				} else if(oldOssPath.indexOf("/online/") > -1) {
					newOssPath = oldOssPath.replace("/online/", "/test/");
				} else {
					return new RespEntity(-2,"初始路径错误，path ："+oldOssPath);
				}
				
				//oss 检查文件是否存在
				if (ossMgr.fileIsExist(newOssPath,gameId)) {
					return new RespEntity(-2,"文件已存在，path:"+newOssPath);
				}
				ossMgr.copyFile(oldOssPath, newOssPath,gameId);
				file.setOssPath(newOssPath);
				log.info("copy file success,packageName:{},sourcePath:{},targetPath:{}",file.getPackName(),oldOssPath,newOssPath);
            } else {
            	if (uploadDao.selectByLocalPath(gameId,targetTable,file.getLocalPath()) != null) {
            		return new RespEntity(-2,"文件已存在");
            	}
            	file.setOssPath("");
            }
			file.setState(IConsts.UpFileState.up2oss.getState());
			uploadDao.copyFile(file, targetTable);
			//保存到redis
			saveToRedis(gameId,file);
			
			String account = (String) request.getSession().getAttribute("account");
			StringBuilder sb = new StringBuilder("app文件发布，游戏id：");
			sb.append(file.getGameId());
			sb.append(",文件名：").append(file.getFileName());
			sb.append(",数据表id：").append(id);
			logMgr.opLogSave(account,OpLogManager.app_log,sb.toString());
		} catch (Exception e) {
			log.error("copyFile error,Exception - > {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		
		return new RespEntity(RespCode.SUCCESS);
	}
	
	private void saveToRedis(int gameId, UploadFile file) {
		String prekey = RedisConsts.PRE_FILE_KEY + gameId;
		try {
			String[] tmp = file.getLocalPath().split("/");
			String filename = tmp[tmp.length - 1];
			redis.hsetPrefix(prekey, RedisConsts.FILE_Map_KEY + "online", filename.substring(0, filename.lastIndexOf(".")), String.valueOf(file.getId()));
		} catch (Exception e) {
			log.error("saveToRedis error,exception : {} ", e);
		}
	}
	
	@PostMapping("/connects") 
    @ResponseBody
    public RespEntity ossConnectlist() {
    	RespEntity res = new RespEntity();
		try {
			List<OssConnect> list = ossDao.getAll();
			res.setData(list);
		} catch (Exception e) {
			log.error("ossConnectlist error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return res;
    }
	
	@PostMapping("/add") 
    @ResponseBody
    public RespEntity ossAdd(@RequestBody OssConnect model,HttpServletRequest request) {
        try {
        	if (!chenkOssConnect(model)) {
        		return new RespEntity(-2,"有空数据");
        	}
        	ossDao.save(model);
        	log.info("ossAdd success,info : {}",StringUtil.objToJson(model));
        	
        	String account = (String) request.getSession().getAttribute("account");
        	StringBuilder sb = new StringBuilder("oss连接新增，内容：");
			sb.append(StringUtil.objToJson(model));
			logMgr.opLogSave(account,OpLogManager.oss_log,sb.toString());
        } catch (Exception e) {
            log.error("ossAdd error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return new RespEntity(RespCode.SUCCESS);
    }
    
    @PostMapping("/update") 
    @ResponseBody
    public RespEntity ossUpdate(@RequestBody OssConnect model,HttpServletRequest request) {

        try {
        	if (!chenkOssConnect(model)) {
        		return new RespEntity(-2,"有空数据");
        	}
        	int id = model.getId();
        	OssConnect old = ossDao.selectById(id);
        	if (old == null) {
        		log.error("OssConnect not find,id : {}",id);
        		return new RespEntity(-2,"更新数据未找到");
        	}
        	ossDao.update(model);
        	log.info("OssConnect update success,json : {}",StringUtil.objToJson(model));
        	
        	if (!model.getDomain().equals(old.getDomain())) {
        		//域名更改
        		List<GameControl> use = gameDao.selectByOssId(id);
        		if (!use.isEmpty()) {
        			//删除对应的redis
        			for (GameControl g : use) {
        				redis.vagueDel(RedisConsts.PRE_LINK_KEY, g.getGameId() + "_*");
        			}
        		}
        	}
        	
        	String account = (String) request.getSession().getAttribute("account");
        	StringBuilder sb = new StringBuilder("oss连接修改，旧内容：");
			sb.append(StringUtil.objToJson(old));
			sb.append(",新内容:");
			sb.append(StringUtil.objToJson(model));
			logMgr.opLogSave(account,OpLogManager.oss_log,sb.toString());
        } catch (Exception e) {
            log.error("ossUpdate error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return new RespEntity(RespCode.SUCCESS);
    }
    
    private boolean chenkOssConnect(OssConnect obj) {
    	if (StringUtil.isEmpty(obj.getAccessKeyId()) || StringUtil.isEmpty(obj.getAccessKeySecret()) || StringUtil.isEmpty(obj.getBucketName())
    			|| StringUtil.isEmpty(obj.getDomain()) || StringUtil.isEmpty(obj.getEndpoint()) ||  StringUtil.isEmpty(obj.getDetail())) {
    		return false;
    	}
    	return true;
    }
    
    @PostMapping("/del") 
    @ResponseBody
    public RespEntity ossDel(@RequestBody OssConnect model,HttpServletRequest request) {

        try {
        	int id = model.getId();
        	OssConnect old = ossDao.selectById(id);
        	if (old == null) {
        		log.error("OssConnect not find,id : {}",id);
        		return new RespEntity(-2,"数据未找到");
        	}
        	//检查是否有game引用
        	if (!gameDao.selectByOssId(id).isEmpty()) {
        		return new RespEntity(-2,"有游戏引用，不可删除");
        	}
        	ossDao.delete(id);
        	log.info("del oss connect success,id : {}",id);
        	
        	String account = (String) request.getSession().getAttribute("account");
        	StringBuilder sb = new StringBuilder("oss连接删除，内容：");
			sb.append(StringUtil.objToJson(old));
			logMgr.opLogSave(account,OpLogManager.oss_log,sb.toString());
        } catch (Exception e) {
            log.error("ossDel error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return new RespEntity(RespCode.SUCCESS);
    }
    
    @PostMapping("/notesUp") 
    @ResponseBody
    public RespEntity notesUp(@RequestBody FileOperation param) {

        try {
        	int id = param.getId();
			int state = param.getGameState();
			String notes = param.getNotes();
			/*if (StringUtil.isEmpty(notes.trim())) {
				return new RespEntity(-2,"备注错误");
			}*/
			String tableName = getTableName(state);
			UploadFile file = uploadDao.selectById(id,tableName);
			if (file == null || file.getState() == IConsts.UpFileState.delete.getState()) {
				return new RespEntity(-2,"文件不存在");
			}
			uploadDao.updateNotes(tableName, id, notes);
			log.debug("备注修改成功，id:{},state : {},notes:{}",id,state,notes);
        } catch (Exception e) {
            log.error("notesUp error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return new RespEntity(RespCode.SUCCESS);
    }
}
