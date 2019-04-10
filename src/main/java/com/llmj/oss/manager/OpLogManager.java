package com.llmj.oss.manager;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.llmj.oss.dao.OpLogDao;
import com.llmj.oss.model.OpLog;
import com.llmj.oss.model.User;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志管理类
 * @author xinghehudong
 *
 */
@Component
@Slf4j(topic = "ossLogger")
public class OpLogManager {
    
	public static int 	game_log 		= 1,			//游戏操作记录
						oss_log 		= 2,			//oss连接管理
						pack_log		= 3,			//包名管理
						file_log		= 4,			//本地文件管理
						up_log			= 5,			//文件上传
						app_log			= 6,			//文件管理
						qr_log			= 7,			//二维码管理
						some_log 		= 100;
	
    @Autowired
    private OpLogDao logDao;
    
    //日志记录
    @Async
    public void opLogSave(String account,int type,String content) {
    	long now = System.currentTimeMillis();
    	try {
			OpLog log = new OpLog();
			log.setOp_account(account);
			log.setOp_type(type);
			log.setOp_time(now);
			log.setOp_content(content);
			logDao.saveLog(log);
		} catch (Exception e) {
			log.error("opLogSave error,Exception - > {}",e);
		}
    }
    
    //日志查询
    
}
