package com.llmj.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.llmj.oss.dao.DomainDao;
import com.llmj.oss.dao.DownDao;
import com.llmj.oss.dao.UploadDao;
import com.llmj.oss.model.Domain;
import com.llmj.oss.model.DownLink;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
	@Autowired
	private DownDao downDao;
	@Autowired
	private DomainDao domainDao;
	
	@GetMapping("/link")
	public String downLink(Model model,HttpServletRequest request) {
		
		try {
			String gameState = request.getParameter("gameState");
			String gameId = request.getParameter("gameId");
			
			String userAgent = request.getHeader("user-agent").toLowerCase();
			String html = "";
			String linkid = "";
			if(userAgent.indexOf("android") != -1){
			    //安卓
				html = "html/links/ffyl/android";
				linkid = gameId + "_" + gameState + "_" + 0;
			}else if(userAgent.indexOf("iphone") != -1 || userAgent.indexOf("ipad") != -1 || userAgent.indexOf("ipod") != -1){
			   //苹果
				html = "html/links/ffyl/ios";
				linkid = gameId + "_" + gameState + "_" + 1;
			}else{
				//userAgent.indexOf("micromessenger")!= -1 微信
			    //电脑
				log.error("request info, userAgent -> {}",userAgent);
				return "html/error";
			}

			//连接配置 动态获取
			DownLink dl = downDao.selectById(linkid);
			if (dl == null || StringUtil.isEmpty(dl.getLink())) {
				log.error("link error, linkid : {}",linkid);
				model.addAttribute("message", "server error!");
				return "html/error";
			}
			//oss域名动态获取
			List<Domain> domains = domainDao.selectByType(1);
			if (domains.isEmpty()) {
				log.error("oss 没有域名存在 数据库为空 ");
				model.addAttribute("message", "server error!");
				return "html/error";
			}
			String link = domains.get(0).getDomain() + dl.getLink();
			model.addAttribute("downlink", link);
			log.info("获得动态链接，link:{}",link);
			return html;
		} catch (Exception e) {
			log.error("downLink error,Exception -> {}",e);
		}
		return "html/error";
	}
	
	@GetMapping("/link1")
	public String downLinkOnline(Model model,HttpServletRequest request) {
		try {
			String gameState = request.getParameter("gameState");
			String gameId = request.getParameter("gameId");
			String type = request.getParameter("type");
			String linkid = gameId + "_" + gameState + "_";
			String html = "html/links/ffyl/android";
			if (type.equals("ios")) {
				html = "html/links/ffyl/ios";
				linkid += 1;
			} else {
				linkid += 0;
			}

			//TODO 连接配置 动态获取
			DownLink dl = downDao.selectById(linkid);
			if (dl == null || StringUtil.isEmpty(dl.getLink())) {
				log.error("link error, linkid : {}",linkid);
				model.addAttribute("message", "server error!");
				return "html/error";
			}
			//TODO oss域名动态获取
			model.addAttribute("downlink", dl.getLink());
			return html;
		} catch (Exception e) {
			log.error("downLink error,Exception -> {}",e);
		}
		return "html/error";
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