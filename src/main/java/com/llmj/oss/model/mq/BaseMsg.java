package com.llmj.oss.model.mq;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseMsg{

	/**
	 * 消息类型 
	 */
	private int msgType = 0;
	/**
	 * 消息队列名
	 */
	private String queueName = "";
	
	
}
