package com.llmj.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.RedisConsts;
import com.llmj.oss.dao.DomainDao;
import com.llmj.oss.dao.DownDao;
import com.llmj.oss.dao.GameControlDao;
import com.llmj.oss.dao.OssConnectDao;
import com.llmj.oss.dao.UploadDao;
import com.llmj.oss.manager.AliOssManager;
import com.llmj.oss.manager.SwitchManager;
import com.llmj.oss.model.Domain;
import com.llmj.oss.model.DownLink;
import com.llmj.oss.model.GameControl;
import com.llmj.oss.model.OssConnect;
import com.llmj.oss.model.UploadFile;
import com.llmj.oss.util.RedisTem;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
	@Autowired
	private DownDao downDao;
	@Autowired
	private DomainDao domainDao;
	@Autowired 
	private AliOssManager ossMgr;
	@Autowired 
	private SwitchManager switchMgr;
	@Autowired 
	private RedisTem redis;
	@Autowired
	private GameControlDao gameDao;
	@Autowired
	private OssConnectDao ossDao;
			
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
				linkid = gameId + "_" + gameState + "_" + IConsts.UpFileType.Android.getType();
			}else if(userAgent.indexOf("iphone") != -1 || userAgent.indexOf("ipad") != -1 || userAgent.indexOf("ipod") != -1){
			   //苹果
				html = "html/links/ffyl/ios";
				linkid = gameId + "_" + gameState + "_" + IConsts.UpFileType.Ios.getType();
			}else{
				//userAgent.indexOf("micromessenger")!= -1 微信
			    //电脑
				log.error("request info, userAgent -> {}",userAgent);
				return "error";
			}
			
			String link = redis.getPre(RedisConsts.PRE_LINK_KEY, linkid);
			if (StringUtil.isEmpty(link)) {
				//连接配置 动态获取
				DownLink dl = downDao.selectById(linkid);
				if (dl == null) {
					log.error("DownLink not find,linkid:{}",linkid);
					model.addAttribute("message", "server error!");
					return "error";
				}
				link = getLink(dl,Integer.parseInt(gameId));
				if ("error".equals(link)) {
					model.addAttribute("message", "server error!");
					return "error";
				}
				redis.setPre(RedisConsts.PRE_LINK_KEY, linkid, link);
			}
			
			model.addAttribute("downlink", link);
			log.info("获得动态链接，link:{}",link);
			return html;
		} catch (Exception e) {
			log.error("downLink error,Exception -> {}",e);
		}
		return "error";
	}
	
	public String getLink(DownLink dl,int gameId) {
		String link = "";
		if (dl == null || StringUtil.isEmpty(dl.getLink())) {
			log.error("DownLink error,dl : {}",StringUtil.objToJson(dl));
			return "error";
		}
		
		if (switchMgr.ossSuccess) {
			//oss域名动态获取
			GameControl game = gameDao.selectById(gameId);
			if (game == null) {
				log.error("GameControl not find,gameId : {} ",gameId);
				return "error";
			}
			OssConnect oss = ossDao.selectById(game.getOssId());
			if (oss == null || StringUtil.isEmpty(oss.getDomain())) {
				log.error("OssConnect error,ossId : {} ",game.getOssId());
				return "error";
			}
			link = oss.getDomain() + "/" + dl.getLink();
		} else {
			//TODO 本地下载
			List<Domain> domains = domainDao.selectByType(0);
			if (domains.isEmpty()) {
				log.error("server 没有域名存在 数据库为空 ");
				return "error";
			}
		}
		return link;
	}
	
	/**
	 * 直接下载文件
	 * @param model
	 * @param request
	 * @return
	 */
	@PostMapping("/file")
	public void singleFileDown(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(name = "fileId") String fileId, @RequestParam(name = "state") String state) {
		try {
    		String tbname = OssController.getTableName(Integer.parseInt(state));
        	UploadFile info = uploadDao.selectById(Integer.parseInt(fileId),tbname);
        	if (info == null) {
        		log.error("UploadFile not find,id -> {}",fileId);
        		return;
        	}
        	//直接转发oss
        	String osslink = ossMgr.ossDomain(info.getGameId()) + "/" + info.getOssPath();
        	response.sendRedirect(osslink);
		} catch (Exception e) {
			log.error("singleFileDown error, Exception -> {}",e);
		}
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