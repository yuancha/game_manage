package com.llmj.oss.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 包名管理
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class PackageName implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int gameId;		//gameId
	private String content;	//游戏名称 作为唯一标识查找gameid 主要用ios查找
	private String android;	//安卓包名
	private String ios;	//ios包名
}
