package com.llmj.oss.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * 现在连接记录
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class DownLink implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;		//gameId + "0 / 1" 0测试 1正式
	private int type;		//0安卓 1ios
	private String link;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date upTime;	//更新时间
	private int targetId;	//对应file表id
}
