package com.llmj.oss.model.oper;

import lombok.Getter;
import lombok.Setter;
/**
 * 二维码操作类
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class QrOperation {
	private int id;	//唯一id
	private int gameId;		//gameId
	private String desc;	//描述
	private int state;		//正式 还是 测试
	private String domain;	//选用域名
	private int middelImg;	//0不带中间图片 1中间带图片
}
