package com.llmj.oss.model.mq;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QrcodeMsg extends BaseMsg{

	/**
	 * 子游戏id
	 */
	private int gameID;
	/**
	 * 二维码链接
	 */
	private String qrLink;
	
}
