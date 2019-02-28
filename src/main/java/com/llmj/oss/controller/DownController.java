package com.llmj.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.llmj.oss.dao.UploadDao;
import com.llmj.oss.model.UploadFile;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 下载管理
 * @author xinghehudong
 *
 */
@Controller
@Slf4j(topic = "ossLogger")
@RequestMapping("/down")
public class DownController {
	
	@Autowired
	private UploadDao uploadDao;
	
	@GetMapping("/testLink")
	public String downLinkTest(Model model) {
		model.addAttribute("message", "this is index jsp page!");
		return "index";
	}
	
	@GetMapping("/onlineLink")
	public String downLinkOnline(Model model) {
		model.addAttribute("message", "this is index html page!");
		return "html/index";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    /*@GetMapping("/downFile/{fileId}") // //new annotation since 4.3
    public void singleFileDown(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable String fileId) {
        if (fileId != null) {
        	try {
        		int id = Integer.parseInt(fileId);
            	UploadFile info = uploadDao.selectById(id);
            	String filePath = info.getLocalPath();
                //设置文件路径
                File file = new File(filePath);
                //File file = new File(realPath , fileName);
                if (file.exists()) {
                    response.setContentType("application/force-download");// 设置强制下载不打开
                    response.addHeader("Content-Disposition", "attachment;fileName=" + info.getGame());// 设置文件名
                    byte[] buffer = new byte[1024];
                    FileInputStream fis = null;
                    BufferedInputStream bis = null;
                    try {
                        fis = new FileInputStream(file);
                        bis = new BufferedInputStream(fis);
                        OutputStream os = response.getOutputStream();
                        int i = bis.read(buffer);
                        while (i != -1) {
                            os.write(buffer, 0, i);
                            i = bis.read(buffer);
                        }
                    } catch (Exception e) {
                    	throw e;
                    } finally {
                        if (bis != null) {
                            try {
                                bis.close();
                            } catch (IOException e) {
                            	throw e;
                            }
                        }
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch (IOException e) {
                                throw e;
                            }
                        }
                    }
                }
			} catch (Exception e) {
				log.error("singleFileDown error, Exception -> {}",e);
			}
        }
    }*/

}