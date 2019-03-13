package com.llmj.oss.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * oss连接管理
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class OssConnect implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String endpoint; 
	private String accessKeyId;
	private String accessKeySecret;
	private String bucketName; 
	private String domain;	//域名连接
	private String detail;	//描述
}
