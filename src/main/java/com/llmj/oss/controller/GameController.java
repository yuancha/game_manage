package com.llmj.oss.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.llmj.oss.config.RedisConsts;
import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.DomainDao;
import com.llmj.oss.dao.GameControlDao;
import com.llmj.oss.manager.MqManager;
import com.llmj.oss.manager.OpLogManager;
import com.llmj.oss.manager.QrcodeManager;
import com.llmj.oss.model.Domain;
import com.llmj.oss.model.GameControl;
import com.llmj.oss.model.QRCode;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.util.RedisTem;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j(topic = "ossLogger")
@RequestMapping("/game")
public class GameController {
	
	@Autowired
	private GameControlDao gameDao;
	@Autowired 
	private RedisTem redis;
	@Autowired 
	private MqManager mqMgr;
	@Autowired
	private OpLogManager logMgr;
	@Autowired
	private DomainDao domainDao;
	@Autowired
	private QrcodeManager qrMgr;
	
    @GetMapping("")
    public String index() {
        return "game";
    }
    
    @PostMapping("/list") 
    @ResponseBody
    public RespEntity gamelist() {
    	RespEntity res = new RespEntity();
		try {
			List<GameControl> list = gameDao.getAll();
			res.setData(list);
		} catch (Exception e) {
			log.error("gamelist error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return res;
    }
    
    @PostMapping("/add") 
    @ResponseBody
    public RespEntity gameAdd(@RequestBody GameControl model,HttpServletRequest request) {
        try {
        	gameDao.save(model);
        	log.info("add game success,info : {}",StringUtil.objToJson(model));
        	String qrChannel = model.getChannel();
        	if (!StringUtil.isEmpty(qrChannel)) {
        		//mq 变更
        		mqMgr.createChannel(model.getGameId(), qrChannel);
        	}
        	String account = (String) request.getSession().getAttribute("account");
        	StringBuilder sb = new StringBuilder("游戏管理新增，内容：");
			sb.append(StringUtil.objToJson(model));
			logMgr.opLogSave(account,OpLogManager.game_log,sb.toString());
        } catch (Exception e) {
            log.error("gameAdd error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return new RespEntity(RespCode.SUCCESS);
    }
    
    @PostMapping("/update") 
    @ResponseBody
    public RespEntity gameUpdate(@RequestBody GameControl model,HttpServletRequest request) {
        try {
        	int gameId = model.getGameId();
        	GameControl old = gameDao.selectById(gameId);
        	if (old == null) {
        		log.error("GameControl not find,gameId : {}",gameId);
        		return new RespEntity(-2,"数据未找到");
        	}
        	gameDao.update(model);
        	log.info("update game success,json : {} ",StringUtil.objToJson(model));
        	
        	String qrChannel = model.getChannel();
        	if (!StringUtil.isEmpty(qrChannel) && !qrChannel.equals(old.getChannel())) {
        		// mq 变更
        		mqMgr.createChannel(model.getGameId(), qrChannel);
        	}
        	
        	if (model.getOssId() != old.getOssId()) {
        		//域名变更 删除redis
        		redis.vagueDel(RedisConsts.PRE_LINK_KEY, gameId + "_*");
        	}
        	
        	String account = (String) request.getSession().getAttribute("account");
        	StringBuilder sb = new StringBuilder("游戏管理修改，旧内容：");
			sb.append(StringUtil.objToJson(old));
			sb.append(",新内容:");
			sb.append(StringUtil.objToJson(model));
			logMgr.opLogSave(account,OpLogManager.game_log,sb.toString());
        } catch (Exception e) {
            log.error("gameUpdate error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return new RespEntity(RespCode.SUCCESS);
    }
    
    //类型0开启 1停止
    @GetMapping("/dm")
    public String domainIndex() {
        return "domain";
    }
    
    @PostMapping("/dm/list") 
    @ResponseBody
    public RespEntity domainlist() {
    	RespEntity result = new RespEntity(RespCode.SUCCESS);
        try {
        	List<Domain> list = domainDao.getAll();
        	result.setData(list);
        } catch (Exception e) {
            log.error("domainlist error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return result;
    }
    
    @PostMapping("/dm/add") 
    @ResponseBody
    public RespEntity domainAdd(@RequestBody Domain model,HttpServletRequest request) {
        try {
        	String domain = model.getDomain().trim();
        	Domain tmp = domainDao.selectById(domain);
        	if (tmp != null) {
        		 return new RespEntity(-2,"域名存在");
        	}
        	
        	Domain use = domainDao.selectByType(0);//正在使用的
        	if (use != null) {
        		model.setType(1);//有正在使用就不允许再创建开放的
        	}
        	
        	domainDao.save(model);
        	log.debug("域名保存成功，内容:{}",StringUtil.objToJson(model));
        	
        	String account = (String) request.getSession().getAttribute("account");
        	StringBuilder sb = new StringBuilder("域名管理新增，内容：");
			sb.append(StringUtil.objToJson(model));
			logMgr.opLogSave(account,OpLogManager.domain_log,sb.toString());
			
			if (model.getType() == 0) {
        		//开启状态
        		//修改二维码通知逻辑服
        		domainChangeOper(domain);
        		StringBuilder sb1 = new StringBuilder("域名管理替换，内容：");
    			sb1.append(StringUtil.objToJson(model));
    			logMgr.opLogSave(account,OpLogManager.domain_log,sb1.toString());
        	}
        } catch (Exception e) {
            log.error("domainAdd error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return new RespEntity(RespCode.SUCCESS);
    }
    
    @PostMapping("/dm/update") 
    @ResponseBody
    public RespEntity domainUpdate(@RequestBody Domain model,HttpServletRequest request) {
        try {
        	String domain = model.getDomain().trim();
        	Domain tmp = domainDao.selectById(domain);
        	if (tmp == null) {
        		return new RespEntity(-2,"数据错误");
        	}
        	
        	String account = (String) request.getSession().getAttribute("account");
        	if (tmp.getType() != 0 && model.getType() == 0) {
        		//域名变更
        		Domain use = domainDao.selectByType(0);//正在使用的.
        		if (use != null) {
        			use.setType(1);
            		domainDao.updateDomain(use);
        		}
        		//二维码一些列变化
        		domainChangeOper(domain);
        		StringBuilder sb1 = new StringBuilder("域名管理替换，内容：");
    			sb1.append(StringUtil.objToJson(model));
    			logMgr.opLogSave(account,OpLogManager.domain_log,sb1.toString());
        	}
        	
        	domainDao.updateDomain(model);
        	log.debug("域名修改成功,info : {}",StringUtil.objToJson(model));
        	
        	StringBuilder sb = new StringBuilder("域名管理修改，旧内容：");
			sb.append(StringUtil.objToJson(tmp));
			sb.append(",新内容:");
			sb.append(StringUtil.objToJson(model));
			logMgr.opLogSave(account,OpLogManager.domain_log,sb.toString());
			
        } catch (Exception e) {
            log.error("domainUpdate error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return new RespEntity(RespCode.SUCCESS);
    }
    
    @PostMapping("/dm/del") 
    @ResponseBody
    public RespEntity domainDel(@RequestBody Domain model,HttpServletRequest request) {
        try {
        	String domain = model.getDomain().trim();
        	Domain tmp = domainDao.selectById(domain);
        	if (tmp == null) {
        		return new RespEntity(-2,"数据错误");
        	}
        	
        	if (tmp.getType() == 0) {
        		return new RespEntity(-2,"启用其它域名后方可删除");
        	}
        	domainDao.delDomain(model);
        	log.debug("域名删除成功，info : {}",StringUtil.objToJson(tmp));
        	
        	String account = (String) request.getSession().getAttribute("account");
        	StringBuilder sb = new StringBuilder("域名管理删除，内容：");
			sb.append(StringUtil.objToJson(tmp));
			logMgr.opLogSave(account,OpLogManager.domain_log,sb.toString());
        } catch (Exception e) {
            log.error("domainDel error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return new RespEntity(RespCode.SUCCESS);
    }
    
    private void domainChangeOper(String domain) {
    	try {
    		List<GameControl> list = gameDao.selectOpens();
    		for (GameControl game : list) {
    			if (game == null || game.getOpen() == 0) {
    				continue;
    			}
    			int gameId = game.getGameId();
    			
    			QRCode qr = qrMgr.getUseQrcode(gameId,1,1);
    			if (qr != null) {
    				//删除二维码
        			qrMgr.qrCodeDel(qr);
        			log.debug("gameId ： {} - > 二维码正式数据删除成功",gameId);
    			}
    			
    			//新增二维码
    			QRCode createOn = new QRCode();
    			createOn.setContent("域名变更自动生成");
    			createOn.setGameId(gameId);
    			createOn.setState(1);
    			qrMgr.saveQr(createOn, domain, 0, null);
    			log.debug("gameId ： {} - > 二维码正式数据新增成功",gameId);
    			
    			QRCode testqr = qrMgr.getUseQrcode(gameId,0,0);
    			if (testqr != null) {
    				qrMgr.qrCodeDel(testqr);
        			log.debug("gameId ： {} - > 二维码测试数据删除成功",gameId);
    			}
    			QRCode createTest = new QRCode();
    			createTest.setContent("域名变更自动生成");
    			createTest.setGameId(gameId);
    			createTest.setState(0);
    			qrMgr.saveQr(createTest, domain, 0, null);
    			log.debug("gameId ： {} - > 二维码测试数据新增成功",gameId);
    		}
		} catch (Exception e) {
			log.error("domainChangeOper Exception - > {}",e);
		}
    }
}