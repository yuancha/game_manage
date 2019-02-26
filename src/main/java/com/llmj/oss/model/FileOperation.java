package com.llmj.oss.model;

import lombok.Getter;
import lombok.Setter;
/**
 * 文件操作类
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class FileOperation {
	private int id;
	private int gameId;
	private int gameType;
	private int page;
	private int limit;
}
