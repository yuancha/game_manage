package com.llmj.oss.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.llmj.oss.model.mq.QrcodeMsg;
import com.llmj.oss.util.StringUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * mq管理类
 * @author xinghehudong
 *
 */
@Component
@Slf4j(topic = "ossLogger")
public class MqManager {
	
	public static void main(String[] args) {
		MqManager mgr = new MqManager();
		try {
			mgr.init();
			mgr.sendMessage("llmj_qrcode_icon_link_"+888888, "hello");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Value("${spring.rabbitmq.host}")
	private String host;
	@Value("${spring.rabbitmq.port}")
	private Integer port;
	@Value("${spring.rabbitmq.username}")
	private String username;
	@Value("${spring.rabbitmq.password}")
	private String password;
	@Value("${spring.rabbitmq.virtual-host}")
	private String virtualHost;
	
	private String channelPrefix = "llmj_qrcode_icon_link_";
	
    
    //初始化队列 创建连接
	private Channel channel;
	private Connection connection;
    private Map<Integer,String> channelMap;
    
    @PostConstruct
    private void init() throws Exception {
    	initQueue();
    	mqInit();
    }
    
    private void initQueue() {
    	//TODO 回头动态加载
    	channelMap = new HashMap<>();
    	channelMap.put(65537, "llmj_qrcode_link");
    	channelMap.put(888888, "llmj_qrcode_link_hb");
    	if (channelMap.isEmpty()) {
    		throw new RuntimeException("加载rabbit mq channel error");
    	}
    }
    
    private void mqInit() throws Exception {
		//配置参数
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setVirtualHost(virtualHost);
		//获取连接
		connection = factory.newConnection();
		channel = connection.createChannel();
		List<String> channelList = new ArrayList<>(channelMap.values());
		for( int nIndex = 0 ; nIndex < channelList.size() ; nIndex++){
			channel.exchangeDeclare(channelList.get(nIndex), "fanout", true, false, null);
			//channel.queueDeclare(endpointName.get(nIndex), false, false, false, null);
			log.debug("create channel success======channelName===={}",channelList.get(nIndex));
		}
    }
    
    public void sendMessage(String strName ,String message) throws IOException {
	    channel.basicPublish(strName, "", null, message.getBytes());
	}	
    
    public void sendQrLinkToLogic(int gameId,String link) throws IOException {
    	String mqName = channelMap.get(gameId);
    	QrcodeMsg msg = new QrcodeMsg();
    	msg.setGameID(gameId);
    	msg.setQueueName(mqName);
    	msg.setQrLink(link);
    	String json = StringUtil.objToJson(msg);
    	sendMessage(mqName,json);
    	log.info("mq 通知成功，mqName：{},gameId:{},json:{}",mqName,gameId,json);
    }
}
