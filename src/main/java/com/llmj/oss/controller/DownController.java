package com.llmj.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.RedisConsts;
import com.llmj.oss.dao.DomainDao;
import com.llmj.oss.dao.DownDao;
import com.llmj.oss.dao.GameControlDao;
import com.llmj.oss.dao.OssConnectDao;
import com.llmj.oss.dao.UploadDao;
import com.llmj.oss.manager.AliOssManager;
import com.llmj.oss.manager.SwitchManager;
import com.llmj.oss.model.DownLink;
import com.llmj.oss.model.GameControl;
import com.llmj.oss.model.OssConnect;
import com.llmj.oss.model.UploadFile;
import com.llmj.oss.util.FileUtil;
import com.llmj.oss.util.RedisTem;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

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
	
	@Value("${upload.local.basePath}")
	private String localPath;
	
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
				html = "html/links/android";
				linkid = gameId + "_" + gameState + "_" + IConsts.UpFileType.Android.getType();
			}else if(userAgent.indexOf("iphone") != -1 || userAgent.indexOf("ipad") != -1 || userAgent.indexOf("ipod") != -1){
			   //苹果
				html = "html/links/ios";
				linkid = gameId + "_" + gameState + "_" + IConsts.UpFileType.Ios.getType();
			}else{
				//userAgent.indexOf("micromessenger")!= -1 微信
			    //电脑
				log.error("request info, userAgent -> {}",userAgent);
				return "error";
			}
			
			String str = redis.getPre(RedisConsts.PRE_HTML_KEY, gameId);
			JSONObject obj = JSON.parseObject(str);
			if (obj != null) {
				model.addAttribute("title", obj.get("title"));
				model.addAttribute("h1", obj.get("h1"));
				model.addAttribute("p1", obj.get("p1"));
				model.addAttribute("p2", obj.get("p2"));
				model.addAttribute("p3", obj.get("p3"));
				model.addAttribute("icon", obj.get("icon"));
			}
			
			String link = redis.getPre(RedisConsts.PRE_LINK_KEY, linkid);
			if (StringUtil.isEmpty(link)) {
				//连接配置 动态获取
				link = getLink(linkid,Integer.parseInt(gameId),request,gameState);
				if ("error".equals(link)) {
					model.addAttribute("message", "server error!");
					return "error";
				}
				redis.setPre(RedisConsts.PRE_LINK_KEY, linkid, link);
			}
			
			model.addAttribute("downlink", link);
			log.debug("获得动态链接，gameId:{},gameState:{},link:{}",gameId,gameState,link);
			return html;
		} catch (Exception e) {
			log.error("downLink error,Exception -> {}",e);
		}
		return "error";
	}
	
	public String getLink(String linkId,int gameId,HttpServletRequest request,String gameState) {
		String link = "error";
		
		DownLink dl = downDao.selectById(linkId);
		if (dl == null || StringUtil.isEmpty(dl.getLink())) {
			log.error("DownLink error,dl : {},linkId:{}",StringUtil.objToJson(dl),linkId);
			return link;
		}
		
		if (switchMgr.getOssSwitch(gameId)) {
			//oss域名动态获取
			GameControl game = gameDao.selectById(gameId);
			if (game == null) {
				log.error("GameControl not find,gameId : {} ",gameId);
				return link;
			}
			OssConnect oss = ossDao.selectById(game.getOssId());
			if (oss == null || StringUtil.isEmpty(oss.getDomain())) {
				log.error("OssConnect error,ossId : {} ",game.getOssId());
				return link;
			}
			link = oss.getDomain() + "/" + dl.getLink();
		} else {
			//本地下载
			StringBuffer url = request.getRequestURL();
			String tmp = url.toString();
			String domain = url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
			if (StringUtil.isEmpty(domain)) {
				log.error("error server domain,url:{}",tmp);
				return link;
			}
			
			link = domain + getLoaclDownPath(dl,gameState); //到本地下载
		}
		return link;
	}
	
	private String getLoaclDownPath(DownLink dl,String gameState) {
		String path = "";
		String tableName = OssController.getTableName(Integer.parseInt(gameState));
		UploadFile file = uploadDao.selectById(dl.getTargetId(),tableName);
		String filePath = file.getLocalPath();
		if (file == null || StringUtil.isEmpty(filePath)) {
			log.error("UploadFile error,info : {}",StringUtil.objToJson(file));
			return path;
		} 
		if (dl.getType() == IConsts.UpFileType.Ios.getType()) {//ios
			filePath += ".plist"; 
		}
		if (!FileUtil.fileExist(filePath)) {
			log.error("本地文件不存在，path : {}",filePath);
			return path;
		}
		path = IConsts.LOCALDOWN + filePath.substring(localPath.length());
		return path;
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
        	/*String link = ossMgr.ossDomain(info.getGameId()) + "/" + info.getOssPath();
        	if (!switchMgr.getOssSwitch(info.getGameId())) {
        		String domain = switchMgr.getUseDomain(info.getGameId(),Integer.parseInt(state));
        		if (!StringUtil.isEmpty(domain)) {
        			link = domain + IConsts.LOCALDOWN + info.getLocalPath().substring(localPath.length());
        		}
        	} */
        	String domain = switchMgr.getUseDomain(info.getGameId(),Integer.parseInt(state));
    		if (StringUtil.isEmpty(domain)) {
    			log.error("domain 获取错误，数据为空");
    			return;
    		}
        	response.sendRedirect(domain + IConsts.LOCALDOWN + info.getLocalPath().substring(localPath.length()));
		} catch (Exception e) {
			log.error("singleFileDown error, Exception -> {}",e);
		}
	}
	
}