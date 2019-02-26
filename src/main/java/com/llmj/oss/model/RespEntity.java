package com.llmj.oss.model;

import com.llmj.oss.config.RespCode;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息返回类
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class RespEntity {
	
	private int code;
	private String message;
	private Object data;
	
	public RespEntity(int code,String message) {
		this.code = code;
		this.message = message;
	}
	
	public RespEntity(RespCode resp) {
		this.code = resp.getCode();
		this.message = resp.getMsg();
	}
	
	public RespEntity(RespCode resp,Object data) {
		this.code = resp.getCode();
		this.message = resp.getMsg();
		this.data = data;
	}
}
