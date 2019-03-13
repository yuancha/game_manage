package com.llmj.oss.model;

import java.io.Serializable;
//import java.util.Date;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * 文件上传记录
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class UploadFile implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private int gameId;
	private String game;
	private String fileName;
	private String packName;
	private String vision;
	private int type;	//0 安卓 1 ios
	private int state;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date upTime;	//上传时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date operTime;	//操作时间
	private String localPath; 	//本地存放路径
	private String ossPath;		//oss存放路径
	//下载路径标识
	
}
