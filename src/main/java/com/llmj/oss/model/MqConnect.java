package com.llmj.oss.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * mq连接信息
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class MqConnect implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int gameId;
	private String host;
	private Integer post;
	private String username;
	private String password;
	private String virtualHost;
	
}
