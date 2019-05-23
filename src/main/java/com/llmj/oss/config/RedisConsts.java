package com.llmj.oss.config;

public class RedisConsts {
	
	public static String PRE_LINK_KEY = "link"; //连接前缀
	public static String PRE_HTML_KEY = "html"; //html模板内容前缀
	public static String OSS_SWITCH_KEY  = "oss_switch";	//oss是否开启 true开启 false本地下载
	public static String PRE_FILE_KEY  = "file_";	//fiel + gameId
	public static String FILE_LIST_KEY  = "file_list";	//file name
	public static String FILE_Map_KEY  = "file_map_";	//name tableid test/online
	public static String ICON_PATH_KEY  = "icon_path";	//图标文件路径 
	
	public static String VIP_LINK_SWITCH_KEY = "vip_link_switch";	//vip_link 是否开发开关 0或无默认开 1管
	public static String VIP_LINK_KEY = "vip_link";		//ios vip链接地址存储 map存储
}
