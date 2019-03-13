package com.llmj.oss.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 游戏管理
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class GameControl implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int gameId;
	private String name;
	private int open;	//0关闭 1开发
	private String channel;		//mq 对应channel
	private int ossId;		//oss 关联id
}
