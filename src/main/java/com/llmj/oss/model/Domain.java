package com.llmj.oss.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 域名
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class Domain implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String domain;
	private int type;	//0有效 1停用
	private String content;
}
