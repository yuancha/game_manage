package com.llmj.oss.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.llmj.oss.config.RedisConsts;
import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.GameControlDao;
import com.llmj.oss.manager.MqManager;
import com.llmj.oss.model.GameControl;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.util.RedisTem;
import com.llmj.oss.util.StringUtil;
import com.rabbitmq.client.Channel;

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
    public RespEntity gameAdd(@RequestBody GameControl model) {
        try {
        	gameDao.save(model);
        	log.info("add game success,info : {}",StringUtil.objToJson(model));
        	String qrChannel = model.getChannel();
        	if (!StringUtil.isEmpty(qrChannel)) {
        		//mq 变更
        		mqMgr.createChannel(model.getGameId(), qrChannel);
        	}
        	
        } catch (Exception e) {
            log.error("gameAdd error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return new RespEntity(RespCode.SUCCESS);
    }
    
    @PostMapping("/update") 
    @ResponseBody
    public RespEntity gameUpdate(@RequestBody GameControl model) {
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
        } catch (Exception e) {
            log.error("gameUpdate error,Exception -> {}",e);
            return new RespEntity(RespCode.SERVER_ERROR);
        }
        return new RespEntity(RespCode.SUCCESS);
    }
}