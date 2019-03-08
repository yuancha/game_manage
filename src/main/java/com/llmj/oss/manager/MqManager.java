package com.llmj.oss.manager;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.llmj.oss.model.MqConnect;
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
			mgr.sendQrLinkToLogic(1, "http://oss/down");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String host = "192.168.1.154";
	private Integer post = 5672;
	private String username = "MyTest1";
	private String password = "123456";
	private String virtualHost = "/";
	private String channelName = "llmj_qrcode_icon_link";
	
    
    //初始化队列 创建连接
    private Object[] creatConnect() throws Exception{
    	Channel channel = null;
    	Connection connection = null;
		//配置参数
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(post);
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setVirtualHost(virtualHost);
		//获取连接
		connection = factory.newConnection();
		channel = connection.createChannel();
    	return new Object[]{channel,connection};
    }
    
    public void sendQrLinkToLogic(int gameId,String qrLink) throws Exception {
    	//TODO 更加gameId获得MqConnect
    	Object[] objs = null;
    	Channel channel = null;
    	Connection connection = null;
    	try {
    		objs = creatConnect();
    		channel = (Channel) objs[0];
    		connection = (Connection) objs[1];
    		channel.basicPublish("", channelName, null, qrLink.getBytes("UTF-8"));
    		log.info("二维码更新到逻辑服成功");
		} catch (Exception e) {
			throw e;
		} finally {
			if (objs != null) {
				if (channel != null)
					try {
						channel.close();
					} catch (Exception e) {
						e.printStackTrace();
					} 
				if (connection != null)
					try {
						connection.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
    	
    }
    
}
