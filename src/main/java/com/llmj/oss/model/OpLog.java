package com.llmj.oss.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpLog implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String op_account;		//操作账号
	private int op_type;		//操作类型
	private String op_content;		//内容
	private long op_time;		//上传时间
}
