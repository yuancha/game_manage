package com.llmj.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.llmj.oss.alioss.AliOssManager;
import com.llmj.oss.config.GlobalBean;
import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.UploadDao;
import com.llmj.oss.mail.DingDingNotice;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.model.UploadFile;
import com.llmj.oss.util.DateUtil;
import com.llmj.oss.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j(topic = "ossLogger")
public class UploadController {
	
	@Autowired
	private DingDingNotice ddnotice;
	@Autowired
	private UploadDao uploadDao;
	@Autowired
	private GlobalBean global;
	@Autowired
	private AliOssManager ossMgr;
	
	//本地存放路径
	@Value("${upload.local.basePath}")
	private String basePath;

	//临时存放地址
	@Value("${upload.local.tmpsave}")
	private String tmpSave;
	
    @GetMapping("/upload")
    public String index() {
        return "upload";
    }

    @PostMapping("/uploadFile") 
    @ResponseBody
    public RespEntity singleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new RespEntity(RespCode.FILE_ERROR);
        }

        try {
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
            
            String filePath = tmpSave + filename;
            byte[] bytes = file.getBytes();
            
            //TOOD 先存储 再删除  此处待优化
            Path path = Paths.get(filePath);
            Files.write(path, bytes);
            
            File fileapk = new File(filePath);
            Map<String,Object> tmpMap =  new HashMap<>();
            if (suffix.equalsIgnoreCase("ipa")) {
            	tmpMap = FileUtil.readIPA(fileapk);
            	tmpMap.put("type", IConsts.UpFileType.Ios.getType());
            } else {
            	tmpMap = FileUtil.readAPK(fileapk);
            	tmpMap.put("type", IConsts.UpFileType.Android.getType());
            }
            FileUtil.deleteFile(filePath);	//删除临时存放
            String packName = (String) tmpMap.get("package");
            //TODO 包名库
            if (packName == null || !global.isContainPackage(packName) ) {
            	return new RespEntity(RespCode.FILE_ERROR);
            }
            
            //最终保存
            String dateStr = DateUtil.getDateStr();
            String ossPath = "";
            if (suffix.equalsIgnoreCase("ipa")) {
            	FileUtil.makeDir(basePath + packName + "/" + IConsts.UpFileType.Ios.getDesc() + "/" + dateStr);
            	filePath = basePath + packName + "/" + IConsts.UpFileType.Ios.getDesc() + "/" + dateStr + "/" +  filename;
            	ossPath = ossMgr.ossTest + packName + "/" + IConsts.UpFileType.Ios.getDesc() + "/" +  filename;
            } else {
            	FileUtil.makeDir(basePath + packName + "/" + IConsts.UpFileType.Android.getDesc() + "/" + dateStr);
            	filePath = basePath + packName + "/" + IConsts.UpFileType.Android.getDesc() + "/" + dateStr + "/" +  filename;
            	ossPath = ossMgr.ossTest + packName + "/" + IConsts.UpFileType.Android.getDesc() + "/" +  filename;
            }
            tmpMap.put("localPath", filePath);
            path = Paths.get(filePath);
            Files.write(path, bytes);
            
            //上传到oss 测试路径
            ossMgr.uploadFileByByte(ossPath,bytes);
            //TODO 自动刷新测试下载链接
            
            tmpMap.put("ossPath", ossPath);
            //日志信息存储
            int tableId = saveUploadLog(tmpMap);
            log.info("upload file success -> info : {}",tmpMap);
            
            //钉钉通知
            /*String msg = filename + "上传成功,下载路径:http://118.89.237.82:9100/downFile/"+tableId;
            ddnotice.noticeGroup(msg);*/
        } catch (Exception e) {
            log.error("singleFileUpload error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return new RespEntity(0,"上传成功");
    }
    
    private int saveUploadLog(Map<String,Object> map) {
    	UploadFile info = new UploadFile();
    	info.setGame(map.get("name").toString());
    	info.setPackName(map.get("package").toString());
    	info.setState(0);
    	info.setVision(map.get("versionName").toString());
    	info.setType(Integer.parseInt(map.get("type").toString()));
    	info.setLocalPath(map.get("localPath").toString());
    	info.setOssPath(map.get("ossPath").toString());
    	info.setState(IConsts.UpFileState.up2oss.getState());
    	uploadDao.saveFile(info,IConsts.UpFileTable.test.getTableName());
    	return info.getId();
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