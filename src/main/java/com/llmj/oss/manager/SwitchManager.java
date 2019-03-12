package com.llmj.oss.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.llmj.oss.model.QRCode;

import lombok.extern.slf4j.Slf4j;

/**
 * 开关管理类
 * @author xinghehudong
 *
 */
@Component
@Slf4j(topic = "ossLogger")
public class SwitchManager {
	
	@Autowired
	private AliOssManager ossMgr;
	
    public boolean ossSuccess = true;		//oss是否正常
    
    public String getQrcodeLink(QRCode qr) {
    	String link = "";
    	if (ossSuccess) {
    		link = ossMgr.ossDomain() + qr.getOssPath();
    	} else {
    		//
    	}
    	qr.setOssPath(link);
    	return link;
    }
}
