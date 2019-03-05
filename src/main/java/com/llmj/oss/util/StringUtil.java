package com.llmj.oss.util;

import java.util.Arrays;

import com.alibaba.fastjson.JSON;
import com.llmj.oss.model.UploadFile;

public class StringUtil {
	
	public static void main(String[] args) {
		String str = "[com.abc.123]";
		str = str.substring(1, str.length() - 1);
		System.out.println(str);
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
	
	public static String objToJson(Object obj) {
		return JSON.toJSONString(obj);
	}
	
}
