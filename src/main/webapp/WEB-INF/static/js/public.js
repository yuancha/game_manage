
//首次进入区分正式服和测试服标识
if($('#app_state').val() == 0){
	$('#showState').text("测试服");
}else if($('#app_state').val() == 1){
	$('#showState').text("正式服");
}





//oss路径名称截取
function filePathSplit(str){
	if(str){
		var name = str.split("/")[4];
		return name;
	}else{
		return "无";
	}
	
}

//小标题更改
function getNavTitle(gameId){
	switch(gameId)
	{
	case "65537":
	  return "内蒙";
	  break;
	case "888888":
	  return "淮北";
	  break;
	case "393217":
	  return "通辽";
	  break;
	case "589825":
	  return "海南";
	  break; 
	case "262145":
	  return "山西";
	  break;
	case "132019":
	  return "齐齐哈尔";
	  break;
	case "459010":
	  return "福建";
	  break;
	case "65793":
	  return "云南";
	  break;
	case "196609":
	  return "我是大A王";
	  break; 
	case "100001":
	  return "发发填大坑";
	  break;
	default:
	  return "未识别";
	}
}

