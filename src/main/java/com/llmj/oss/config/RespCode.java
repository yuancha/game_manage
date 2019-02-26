package com.llmj.oss.config;

public enum RespCode {
	
	SERVER_ERROR(-1, "网络异常，请稍后重试"),
	SUCCESS(0, "请求成功"),
	GAME_PACKAGE(1,"游戏数据包错误"),
	FILE_ERROR(2,"文件格式错误"),
	;

    private int code;
    private String msg;

    RespCode(int code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
