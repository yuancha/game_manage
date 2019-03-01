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
public class FileOperation {
	private int id;
	private int gameId;		//游戏id
	private int gameType;	//游戏类型 0安卓 1ios
	private int gameState;	//状态 0test 1正式
	private int page;
	private int limit;
}
