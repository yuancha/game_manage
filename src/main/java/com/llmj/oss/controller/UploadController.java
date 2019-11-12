package com.llmj.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.RedisConsts;
import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.GameControlDao;
import com.llmj.oss.dao.UploadDao;
import com.llmj.oss.mail.DingDingNotice;
import com.llmj.oss.manager.AliOssManager;
import com.llmj.oss.manager.OpLogManager;
import com.llmj.oss.manager.PackManager;
import com.llmj.oss.manager.SwitchManager;
import com.llmj.oss.model.GameControl;
import com.llmj.oss.model.PackageName;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.model.UploadFile;
import com.llmj.oss.util.DateUtil;
import com.llmj.oss.util.FileUtil;
import com.llmj.oss.util.HttpClientUtil;
import com.llmj.oss.util.RedisTem;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j(topic = "ossLogger")
public class UploadController {
	
	@Autowired
	private DingDingNotice ddnotice;
	@Autowired
	private UploadDao uploadDao;
	@Autowired
	private PackManager packMgr;
	@Autowired
	private AliOssManager ossMgr;
	@Autowired
	private GameControlDao gameDao;
	@Autowired
	private SwitchManager switchMgr;
	@Autowired 
	private RedisTem redis;
	@Autowired
	private OpLogManager logMgr;
	
	//本地存放路径
	@Value("${upload.local.basePath}")
	private String basePath;

	//临时存放地址
	@Value("${upload.local.tmpsave}")
	private String tmpSave;
	
    @GetMapping("/upload")
    public String index() {
        return "html/upload";
    }
    
    /*
     * 只允许上传ipa apk格式文件
     */
    @PostMapping("/uploadFile") 
    @ResponseBody
    public RespEntity singleFileUpload(@RequestParam("file") MultipartFile file,HttpServletRequest request,@RequestParam("upstate") Integer state) {
        if (file.isEmpty()) {
            return new RespEntity(RespCode.FILE_ERROR);
        }
        
        try {
        	String account = (String) request.getSession().getAttribute("account");
        	String osspthstr = ossMgr.ossTest;
        	String tbname = IConsts.UpFileTable.test.getTableName();
        	
        	if (!StringUtil.isEmpty(account) && state == 1) {//账号不为空方可上传正式文件
        		osspthstr = ossMgr.ossOnline;
        		tbname = IConsts.UpFileTable.online.getTableName();
        	}
        	
            String filename = file.getOriginalFilename();
            String[] tmpary = filename.split("\\.");
            if (tmpary == null || tmpary.length < 2) {
            	log.error("upload file name error -> {}",filename);
            	return new RespEntity(RespCode.FILE_ERROR);
            }
            
            String suffix = tmpary[tmpary.length - 1];
            if (!suffix.equalsIgnoreCase("ipa") && !suffix.equalsIgnoreCase("apk")) {
            	log.error("upload file type error -> {}",suffix);
            	return new RespEntity(RespCode.FILE_ERROR);
            }
            
            File tmp = new File(tmpSave);
            if (!tmp.exists() || !tmp.isDirectory()) {
            	tmp.mkdir();
            }
            String filePath = tmpSave + filename;
            byte[] bytes = file.getBytes();
            
            //TOOD 先存储 再删除  此处待优化
            Path path = Paths.get(filePath);
            Files.write(path, bytes);
            
            File fileapk = new File(filePath);
            Map<String,Object> tmpMap =  new HashMap<>();
            int type = 0;
            if (suffix.equalsIgnoreCase("ipa")) {
            	tmpMap = FileUtil.readIPA(fileapk);
            	tmpMap.put("type", IConsts.UpFileType.Ios.getType());
            	type = IConsts.UpFileType.Ios.getType();
            } else {
            	tmpMap = FileUtil.readAPK(fileapk);
            	tmpMap.put("type", IConsts.UpFileType.Android.getType());
            	type = IConsts.UpFileType.Android.getType();
            }
            FileUtil.deleteFile(filePath);	//删除临时存放
            String packName = (String) tmpMap.get("package");
            String gameName = (String) tmpMap.get("name");
            //包名验证
            PackageName pn = packMgr.getPackInfo(packName,type, gameName);
            if (packName == null || pn == null) {
            	log.error("文件非法，未找到对应包信息, packName : {}, gameName : {}",packName, gameName);
            	return new RespEntity(-2,"文件包信息错误，包名 ："+packName+ " 游戏名 : "+gameName);
            }
            
            int gameId = pn.getGameId();
            GameControl gc = gameDao.selectById(gameId);
            if (gc == null || gc.getOpen() == 0) {
            	return new RespEntity(-2,"游戏不存在，或是已关闭");
            }
            //最终保存
            String dateStr = DateUtil.getDateStr();
            String ossPath = "";
            //正规命名
            String gmPy = packName.split("\\.")[1];
            String saveName = gmPy+"_"+tmpMap.get("versionName").toString()+"_" + dateStr;
            if (suffix.equalsIgnoreCase("ipa")) {
            	FileUtil.makeDir(basePath + IConsts.UpFileType.Ios.getDesc() + "/" + packName + "/" + dateStr);
            	filePath = basePath + IConsts.UpFileType.Ios.getDesc() + "/" + packName + "/" + dateStr + "/" +  saveName + ".ipa";
            	ossPath = osspthstr + IConsts.UpFileType.Ios.getDesc() + "/" + packName + "/" +  saveName + ".ipa";
            } else {
            	FileUtil.makeDir(basePath + IConsts.UpFileType.Android.getDesc() + "/" + packName + "/" + dateStr);
            	filePath = basePath + IConsts.UpFileType.Android.getDesc() + "/" + packName + "/" + dateStr + "/" +  saveName + ".apk";
            	ossPath = osspthstr + IConsts.UpFileType.Android.getDesc() + "/" + packName + "/" +  saveName + ".apk";
            }
            tmpMap.put("localPath", filePath);
            
            path = Paths.get(filePath);
            Files.write(path, bytes);
            //上传到oss 测试路径
            tmpMap.put("ossPath", "");
            if (switchMgr.getOssSwitch(gameId)) {//oss 服务正常
            	ossMgr.uploadFileByByte(ossPath,bytes,gameId);
            	tmpMap.put("ossPath", ossPath);
            }
            tmpMap.put("gameId", gameId);
            tmpMap.put("fileName", filename);
            tmpMap.put("tbname", tbname);
            //数据信息存储
            int tableId = saveUploadLog(tmpMap);
            //redis存储 用于文件管理
            saveToRedis(gameId,saveName,tableId);
            //TODO 某种通知方式
            
            upFileLog(request,tmpMap);
            StringBuilder succInfo = new StringBuilder();
            succInfo.append("上传成功。");
            succInfo.append(" 游戏id:").append(gameId);
            succInfo.append(" 游戏名:").append(gameName);
            succInfo.append(" 包名：").append(packName);
            return new RespEntity(0, succInfo.toString());
        } catch (Exception e) {
            log.error("singleFileUpload error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
    }
    
    private int saveUploadLog(Map<String,Object> map) {
    	UploadFile info = new UploadFile();
    	info.setGame(map.get("name").toString());
    	info.setPackName(map.get("package").toString());
    	info.setVision(map.get("versionName").toString());
    	info.setType(Integer.parseInt(map.get("type").toString()));
    	info.setLocalPath(map.get("localPath").toString());
    	info.setOssPath(map.get("ossPath").toString());
    	info.setState(IConsts.UpFileState.up2oss.getState());
    	info.setFileName(map.get("fileName").toString());
    	info.setGameId(Integer.parseInt(map.get("gameId").toString()));
    	uploadDao.saveFile(info,map.get("tbname").toString());
    	log.info("upload file success -> info : {}",StringUtil.objToJson(info));
    	return info.getId();
    }
    
    private void saveToRedis(int gameId,String saveName,int tableId) {
    	String prekey = RedisConsts.PRE_FILE_KEY + gameId;
    	try {
			redis.lpushPre(prekey, RedisConsts.FILE_LIST_KEY, saveName);
			redis.hsetPrefix(prekey, RedisConsts.FILE_Map_KEY+"test", saveName, String.valueOf(tableId));
		} catch (Exception e) {
			log.error("saveToRedis error,exception : {} ",e);
		}
    }
    
    private void upFileLog(HttpServletRequest request,Map<String,Object> tmpMap) {
    	try {
            String account = (String) request.getSession().getAttribute("account");
        	if (StringUtil.isEmpty(account)) {
        		account = HttpClientUtil.getIpAddr(request);
        	} 
            
            StringBuilder sb = new StringBuilder("app文件上传，保存内容：");
			sb.append(StringUtil.objToJson(tmpMap));
			logMgr.opLogSave(account,OpLogManager.up_log,sb.toString());
		} catch (Exception e) {
			log.error("upFileLog error,Exception - > {}",e);
		}
    }
    
    /**
     * 上传对应的plist html模板
     */
    @PostMapping("/uploadOther") 
    @ResponseBody
    public RespEntity otherFileUpload(@RequestParam("file") MultipartFile file) {
    	
    	return new RespEntity(0,"上传成功");
    }
}