package com.llmj.oss.model.oper;

import lombok.Getter;
import lombok.Setter;
/**
 * 文件操作类
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class PackOperation {
	private int gameId;		//gameId
	private String desc;	//描述
	private String android;	//安卓包名
	private String ios;	//ios包名
}
