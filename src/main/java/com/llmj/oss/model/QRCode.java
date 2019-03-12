package com.llmj.oss.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 二维码
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class QRCode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;	//自增id
	private int gameId;	//游戏id
	private int state;	//测试或是正式 0 1
	private String content;	//描述
	private String link;	//链接
	private String photo;	//图片二进制
	private String ossPath;	//oss 二维码图片路径
	private String localPath;	//本地 保存路径
	private int logicUse;	//1为备份到逻辑服
}
