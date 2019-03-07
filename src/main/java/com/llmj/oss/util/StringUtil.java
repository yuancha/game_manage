package com.llmj.oss.util;

import java.util.UUID;

import com.alibaba.fastjson.JSON;

public class StringUtil {
	
	public static void main(String[] args) {
		System.out.println(getUUIDStr());
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
	
	public static String objToJson(Object obj) {
		return JSON.toJSONString(obj);
	}
	
	public static String getUUIDStr() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
