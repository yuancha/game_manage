package com.llmj.oss.manager;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.llmj.oss.config.IConsts;
import com.llmj.oss.dao.GameControlDao;
import com.llmj.oss.dao.OssConnectDao;
import com.llmj.oss.model.GameControl;
import com.llmj.oss.model.OssConnect;
import com.llmj.oss.model.UploadFile;
import com.llmj.oss.util.FileUtil;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "ossLogger")
public class AliOssManager {
	
	@Autowired
	private GameControlDao gameDao;
	@Autowired
	private OssConnectDao ossDao;
	@Autowired
	private SwitchManager switchMgr;
	
    @Value("${upload.oss.test}")
    public String ossTest;
    @Value("${upload.oss.online}")
    public String ossOnline;
    @Value("${upload.local.basePath}")
	private String localPath;
    
    private OssConnect getOssConnectInfo(int gameId) {
    	GameControl game = gameDao.selectById(gameId);
    	return ossDao.selectById(game.getOssId());
    }
    
    //暂时每次都新建 也可以设置成单列（可并发使用）
    private OSSClient getClient(OssConnect oss) throws Exception {
    	OSSClient ossClient = new OSSClient(oss.getEndpoint(), oss.getAccessKeyId(), oss.getAccessKeySecret());
    	if (ossClient.doesBucketExist(oss.getBucketName())) {
            //System.out.println("您已经创建Bucket：" + bucketName + "。");
        } else {
        	log.error("OssConnect info ： {}",StringUtil.objToJson(oss));
        	throw new Exception("Bucket不存在,"+oss.getBucketName());
        }
    	return ossClient;
    }
    
    public String ossDomain(int gameId) throws Exception {
    	OssConnect oss = getOssConnectInfo(gameId);
    	if (oss == null) {
    		throw new Exception("OssConnect not fine,gameId : "+gameId);
    	}
    	return oss.getDomain();
    }
    
    public boolean uploadFile(String ossPath,String localPath,int gameId) throws Exception {
    	OSSClient ossClient = null;
    	boolean success = false;
    	try {
    		OssConnect oss =  getOssConnectInfo(gameId);
    		ossClient = getClient(oss);
    		PutObjectResult  result = ossClient.putObject(oss.getBucketName(), ossPath, new File(localPath));
        	log.debug("上传文件成功，"+ossPath);
        	success = true;
    	} finally {
			if (ossClient != null) 
				ossClient.shutdown();
		}
    	return success;
    }
    
    public boolean downFile(String ossPath,String localPath,int gameId) throws Exception {
    	OSSClient ossClient = null;
    	boolean success = false;
    	try {
    		OssConnect oss =  getOssConnectInfo(gameId);
    		ossClient = getClient(oss);
    		ObjectMetadata  result = ossClient.getObject(new GetObjectRequest(oss.getBucketName(), ossPath), new File(localPath));
        	log.debug("下载文件成功，"+ossPath);
        	success = true;
    	} finally {
			if (ossClient != null) 
				ossClient.shutdown();
		}
    	return success;
    }
    
    public void removeFile(String ossPath,int gameId) throws Exception {
    	OSSClient ossClient = null;
    	try {
    		OssConnect oss =  getOssConnectInfo(gameId);
    		ossClient = getClient(oss);
    		ossClient.deleteObject(oss.getBucketName(), ossPath);
    		log.debug("删除文件成功，path :{}",ossPath);
		} finally {
        	if (ossClient != null) 
				ossClient.shutdown();
		}
    }
    
    public boolean uploadFileByByte(String ossPath,String str,int gameId) throws Exception {
    	return uploadFileByByte(ossPath,str.getBytes(),gameId);
    }
    
    public boolean uploadFileByByte(String ossPath,byte[] content,int gameId) throws Exception {
    	OSSClient ossClient = null;
    	boolean success = false;
    	try {
    		OssConnect oss =  getOssConnectInfo(gameId);
    		ossClient = getClient(oss);
    		PutObjectResult  result = ossClient.putObject(oss.getBucketName(), ossPath, new ByteArrayInputStream(content));
        	log.debug("上传byte数据成功，"+ossPath);
        	success = true;
    	} finally {
			if (ossClient != null) 
				ossClient.shutdown();
		}
    	return success;
    }
    
    /**
     * 判断文件是否存在
     * @param ossPath
     * @return
     * @throws Exception 
     */
    public boolean fileIsExist(String ossPath,int gameId) throws Exception {
    	OSSClient ossClient = null;
    	boolean success = false;
    	try {
    		OssConnect oss =  getOssConnectInfo(gameId);
    		ossClient = getClient(oss);
    		success = ossClient.doesObjectExist(oss.getBucketName(), ossPath);
    	} finally {
			if (ossClient != null) 
				ossClient.shutdown();
		}
    	return success;
    }
    
	public String getOssBasePath (int state) {
		if (state == 0) {//测试
			return ossTest;
		}
		return ossOnline;
	}
	
	/**
	 * 复制文件
	 * @param ossPath
	 * @return
	 * @throws Exception 
	 */
	public boolean copyFile(String sourcePath,String targetPath,int gameId) throws Exception {
    	OSSClient ossClient = null;
    	boolean success = false;
    	try {
    		OssConnect oss =  getOssConnectInfo(gameId);
    		ossClient = getClient(oss);
    		ossClient.copyObject(oss.getBucketName(), sourcePath, oss.getBucketName(), targetPath);
    		success = true;
    	} finally {
			if (ossClient != null) 
				ossClient.shutdown();
		}
    	return success;
    }
    
	/**
	 * 替换plist指定内容
	 * @throws Exception 
	 */
	public void changePlist(String content,String ossIpaPath,String ossPlistPath,UploadFile file,int gameId) throws Exception {
		OssConnect oss =  getOssConnectInfo(gameId);
		String sb = oss.getDomain() + "/" + ossIpaPath;
		String change = content.replaceAll("<string>http:.*</string>", "<string>"+sb+"</string>");//plist path
		change = change.replaceAll("<string>com.*</string>", "<string>"+file.getPackName()+"</string>");
		change = change.replaceAll("<string>六六.*</string>", "<string>"+file.getGame()+"</string>");
		uploadFileByByte(ossPlistPath,change,gameId);
		log.info("oss plist 操作成功,oss保存路径->{}",ossPlistPath);
	}
	
	public void changeLocalPlist(String content,String localIpaPath,UploadFile file) throws Exception {
		String domain = switchMgr.getUseDomain(file.getGameId(),1);//以正式服使用为主 因为正式和测试通用一个本地plist
		String sb = domain + IConsts.LOCALDOWN + localIpaPath.substring(localPath.length());
		String change = content.replaceAll("<string>http:.*</string>", "<string>"+sb+"</string>");//plist path
		change = change.replaceAll("<string>com.*</string>", "<string>"+file.getPackName()+"</string>");
		change = change.replaceAll("<string>六六.*</string>", "<string>"+file.getGame()+"</string>");
		String saveLocalPath = localIpaPath + ".plist";
		FileUtil.stringToFile(change,saveLocalPath);
		log.info("本地 plist 操作成功，本地保存路径->{}",saveLocalPath);
	}
}
