package com.llmj.oss.alioss;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.llmj.oss.config.IConsts;
import com.llmj.oss.model.UploadFile;
import com.llmj.oss.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "ossLogger")
public class AliOssManager {
	
	public static void main(String[] args) {
		AliOssManager mgr = new AliOssManager();
		String source = "yctest/test/com.ffylmj.taiding/android/ffylmj_formal_20190215_03.apk";
		String target = "yctest/online/com.ffylmj.taiding/android/ffylmj_formal_20190215_01.apk";
		System.out.println(mgr.fileIsExist(source));
	}
	
	private static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
	private static String endpoints = "https://oss-cn-beijing.aliyuncs.com";
	private static String accessKeyId = "LTAIMO9xn64DcMJ6";
    private static String accessKeySecret = "lpcoQYwaiv7LugHOUe8y0LiWjf6r6G";
    private static String bucketName = "bj-fftl-tongliao";
    
    @Value("${upload.oss.test}")
    public String ossTest;
    @Value("${upload.oss.online}")
    public String ossOnline;
    
    //暂时每次都新建 也可以设置成单列（可并发使用）
    private OSSClient getClient() {
    	OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
    	if (ossClient.doesBucketExist(bucketName)) {
            //System.out.println("您已经创建Bucket：" + bucketName + "。");
        } else {
            //System.out.println("您的Bucket不存在，创建Bucket：" + bucketName + "。");
            //ossClient.createBucket(bucketName);
        	throw new RuntimeException("Bucket不存在,"+bucketName);
        }
    	return ossClient;
    }
    
    public boolean uploadFile(String ossPath,String localPath) {
    	OSSClient ossClient = null;
    	boolean success = false;
    	try {
    		ossClient = getClient();
    		PutObjectResult  result = ossClient.putObject(bucketName, ossPath, new File(localPath));
    		//TODO result判断是否成功
        	log.debug("上传文件成功，"+ossPath);
        	success = true;
    	} catch (Exception e) {
        	log.error("oss uploadFile error,Exception -> {}",e);
        } finally {
			if (ossClient != null) 
				ossClient.shutdown();
		}
    	return success;
    }
    
    public boolean downFile(String ossPath,String localPath) {
    	OSSClient ossClient = null;
    	boolean success = false;
    	try {
    		ossClient = getClient();
    		ObjectMetadata  result = ossClient.getObject(new GetObjectRequest(bucketName, ossPath), new File(localPath));
    		//TODO result判断是否成功
        	log.debug("下载文件成功，"+ossPath);
        	success = true;
    	} catch (Exception e) {
        	log.error("oss downFile error,Exception -> {}",e);
        } finally {
			if (ossClient != null) 
				ossClient.shutdown();
		}
    	return success;
    }
    
    public void removeFile(String ossPath) {
    	OSSClient ossClient = getClient();
    	try {
    		ossClient.deleteObject(bucketName, ossPath);
    		log.debug("删除文件成功，path :{}",ossPath);
		} catch (Exception e) {
        	log.error("oss removeFile error,Exception -> {}",e);
        } finally {
        	if (ossClient != null) 
				ossClient.shutdown();
		}
    }
    
    public boolean uploadFileByByte(String ossPath,String str) {
    	return uploadFileByByte(ossPath,str.getBytes());
    }
    
    public boolean uploadFileByByte(String ossPath,byte[] content) {
    	OSSClient ossClient = null;
    	boolean success = false;
    	try {
    		ossClient = getClient();
    		PutObjectResult  result = ossClient.putObject(bucketName, ossPath, new ByteArrayInputStream(content));
    		//TODO result判断是否成功
        	log.debug("上传byte数据成功，"+ossPath);
        	success = true;
    	} catch (Exception e) {
        	log.error("oss uploadFileByByte error,Exception -> {}",e);
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
     */
    public boolean fileIsExist(String ossPath) {
    	OSSClient ossClient = null;
    	boolean success = false;
    	try {
    		ossClient = getClient();
    		success = ossClient.doesObjectExist(bucketName, ossPath);
    	} catch (Exception e) {
        	log.error("oss fileIsExist error,Exception -> {}",e);
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
	 */
	public boolean copyFile(String sourcePath,String targetPath) {
    	OSSClient ossClient = null;
    	boolean success = false;
    	try {
    		ossClient = getClient();
    		ossClient.copyObject(bucketName, sourcePath, bucketName, targetPath);
    		success = true;
    	} catch (Exception e) {
        	log.error("oss copyFile error,Exception -> {}",e);
        } finally {
			if (ossClient != null) 
				ossClient.shutdown();
		}
    	return success;
    }
    
	/**
	 * 替换plist指定内容
	 */
	public void changePlist(String content,String saveLocalPath,String ossIpaPath,String ossPlistPath,UploadFile file) {
		try {
			String change = content.replaceAll("<string>http:.*</string>", "<string>"+endpoint+"/"+ossIpaPath+"</string>");//plist path
			change = change.replaceAll("<string>com.*</string>", "<string>"+file.getPackName()+"</string>");
			change = change.replaceAll("<string>六六.*</string>", "<string>"+file.getGame()+"</string>");
			FileUtil.stringToFile(change,saveLocalPath);
			uploadFileByByte(ossPlistPath,change);
			log.info("plist 操作成功，本地报错路径->{},oss保存路径->{}",saveLocalPath,ossPlistPath);
		} catch (Exception e) {
			log.error("changePlist error,Exception --> {}",e);
		}
	}
	
	/**
	 * 替换html指定内容
	 */
	public void changeHtml(String content,String replace,String ossPath) {
		try {
			String change = content.replaceAll("url=.*plist", "url="+endpoints+"/"+replace);
			uploadFileByByte(ossPath,change);
			log.info("html 操作成功，oss保存路径 ->{}",ossPath);
		} catch (Exception e) {
			log.error("changeHtml error,Exception --> {}",e);
		}
	}
}
