package com.llmj.oss.util;

import java.util.Arrays;

import com.alibaba.fastjson.JSON;
import com.llmj.oss.model.UploadFile;

public class StringUtil {
	
	public static void main(String[] args) {
		String str = "com.abc.123";
		System.out.println(Arrays.toString(str.split("\\.")));
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
	
	public static String objToJson(Object obj) {
		return JSON.toJSONString(obj);
	}
	
}
