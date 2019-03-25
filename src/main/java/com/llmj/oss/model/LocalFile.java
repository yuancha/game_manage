package com.llmj.oss.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 本地文件展示
 * @author xinghehudong
 *
 */
@Getter
@Setter
public class LocalFile {

	private String fileName;
	private String upName;
	private int testState;
	private int onlineState;
}
