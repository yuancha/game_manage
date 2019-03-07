
//首次进入区分正式服和测试服标识
if($('#app_state').val() == 0){
	$('#showState').text("测试服");
}else if($('#app_state').val() == 1){
	$('#showState').text("正式服");
}


//主导航条
$('#main-menu').find('a').on('click',function(){
	$(this).parents('li').addClass('active').siblings("li").removeClass('active');
	
	var gameId 		= $(this).attr('id');
	var gameType 	= "0";
	var gameState 	= $('#app_state').val();
	var data 		= '{"gameId":"'+gameId+'","gameType":"'+gameType+'","gameState":"'+gameState+'"}';
	$('#gameId').val(gameId);
	$('.page-title').find('.active').find('a').html(getNavTitle(gameId));
	getFilesInfoByAjax(data); 
});



$(document).on('click','.ev_look',function(){
	$('#modal-2').modal('show', {backdrop: 'fade'});
	$('#modal-2').find('.modal-body').children().remove();
	var app_localPath 	= $(this).parents('tr').find('.app_localPath').val();
	var app_operTime 	= $(this).parents('tr').find('.app_operTime').val();
	var app_ossPath 	= $(this).parents('tr').find('.app_ossPath').val();
	var app_packName 	= $(this).parents('tr').find('.app_packName').val();
	var app_state 		= $(this).parents('tr').find('.app_state').val();
	var app_type 		= $(this).parents('tr').find('.app_type').val();
	var app_upTime 		= $(this).parents('tr').find('.app_upTime').val();
	var app_vision 		= $(this).parents('tr').find('.app_vision').val();
	var app_game		= $(this).parents('tr').find('.app_game').val();
	var app_id 			= $(this).parents('tr').find('.app_id').val();

	if(app_type == 0){
		var type = "安卓";
	}else if(app_type == 1){
		var type = "苹果";
	}
	$('#modal-2').find('.modal-body').html("\
			<p>名称："+app_game+"</p>\
			<p>包名："+app_packName+"</p>\
			<p>版本："+app_vision+"</p>\
			<p>系统："+type+"</p>\
			<p>状态："+app_state+"</p>\
			<p>更新时间："+app_upTime+"</p>\
			<input type='hidden' id='show_app_id' value='"+app_id+"'>\
	");
	
});



//游戏包系统切换

$('#app_android,#app_iOS').on('click',function(){
	var gameId  = $('#gameId').val();
	var gameType = $(this).attr('id');
	if(gameType == "app_android"){
		gameType   = "0";
		$('#app_system').text("Android");
	}else if(gameType == "app_iOS"){
		gameType   = "1";
		$('#app_system').text("iOS");
	}
	var gameState = $('#app_state').val();
	var data 		= '{"gameId":"'+gameId+'","gameType":"'+gameType+'","gameState":"'+gameState+'"}';
	
	console.log(data);
	getFilesInfoByAjax(data); 
});


//刷包
$(document).on('click','.ev_refresh',function(){
	var id 			= $(this).parents('.modal-content').find('#show_app_id').val();
	var gameState 	= $('#app_state').val();
	var data 		= '{"id":"'+id+'","gameState":"'+gameState+'"}';

	refreshFilesInfoByAjax(data); 
});

function refreshFilesInfoByAjax(data){
	$.ajax({
		 //几个参数需要注意一下
      type: "POST",//方法类型
      dataType: "json",//预期服务器返回的数据类型
      contentType: "application/json; charset=utf-8",
      url: "/oss/refreshPack" ,//url
      data: data,
      success: function (result) {
          console.log(result);//打印服务端返回的数据(调试用)
          if (result.code == 0) {
              alert("刷包成功");    
          }else{
       	   	  alert(result.message);
          }
      },
      error : function() {
          alert("异常！");
      }
  });
}



//拷贝
$(document).on('click','.app_refresh',function(){
	var id 			= $(this).parents('tr').find('.app_id').val();
	var gameState 	= $('#app_state').val();
	var data 		= '{"id":"'+id+'","gameState":"'+gameState+'"}';
	console.log(data);
	copyFilesInfoByAjax(data); 
});

function copyFilesInfoByAjax(data){
	$.ajax({
		 //几个参数需要注意一下
       type: "POST",//方法类型
       dataType: "json",//预期服务器返回的数据类型
       contentType: "application/json; charset=utf-8",
       url: "/oss/copyFile" ,//url
       data: data,
       success: function (result) {
           console.log(result);//打印服务端返回的数据(调试用)
           if (result.code == 0) {
               alert("拷贝成功");    
           }else if(result.code == -2){
        	   alert("文件已存在");
           }
       },
       error : function() {
           alert("异常！");
       }
   });
}

//删除
$(document).on('click','#app_delete',function(){
	var id 		= $(this).parents('.modal-content').find('#show_app_id').val();
	var gameState 	= $('#app_state').val();
	var data 		= '{"id":"'+id+'","gameState":"'+gameState+'"}';
	deleteFilesInfoByAjax(data); 
});

function deleteFilesInfoByAjax(data){
	$.ajax({
		 //几个参数需要注意一下
      type: "POST",//方法类型
      dataType: "json",//预期服务器返回的数据类型
      contentType: "application/json; charset=utf-8",
      url: "/oss/delFile" ,//url
      data: data,
      success: function (result) {
          console.log(result);//打印服务端返回的数据(调试用)
          if (result.code == 0) {
              alert("删除成功");    
          }else{
        	  alert(result.message);
          }
      },
      error : function() {
          alert("异常！");
      }
  });
}

//获取游戏包数据详情
function getFilesInfoByAjax(data){	
	$('#online_osspath').text("");
	$('#online_version').text("");
    $('#online_operTime').text("");
    $('#online_upTime').text("");
	$.ajax({
		 //几个参数需要注意一下
        type: "POST",//方法类型
        dataType: "json",//预期服务器返回的数据类型
        contentType: "application/json; charset=utf-8",
        url: "/oss/getFilesInfo" ,//url
        data: data,
        success: function (result) {
            console.log(result);//打印服务端返回的数据(调试用)
            $('#app_box').children().remove();
            if (result.code == 0) {
                //alert("SUCCESS!啦啦啦");
                var data = result.data;
                for(var i in data){
               
               	 $('#app_box').append("<tr>\
               		<td class='middle-align'>"+filePathSplit(data[i].ossPath)+"</td>\
                       <td class='middle-align'>"+data[i].vision+"</td>\
                       <td>\
                           <a href='#' class='btn btn-secondary btn-single btn-sm ev_look'>查看</a>\
                           <a href='#' class='btn btn-turquoise btn-single btn-sm ev_refresh'>刷包</a>\
                       </td>\
                       <input type='hidden' value="+data[i].id+" class='app_id' >\
                       <input type='hidden' value="+data[i].game+" class='app_game' >\
                       <input type='hidden' value="+data[i].localPath+" class='app_localPath' >\
                       <input type='hidden' value="+data[i].operTime+" class='app_operTime' >\
                       <input type='hidden' value="+data[i].ossPath+" class='app_ossPath' >\
                       <input type='hidden' value="+data[i].packName+" class='app_packName' >\
                       <input type='hidden' value="+data[i].state+" class='app_state' >\
                       <input type='hidden' value="+data[i].type+" class='app_type' >\
                       <input type='hidden' value="+data[i].upTime+" class='app_upTime' >\
                       <input type='hidden' value="+data[i].vision+" class='app_vision' >\
                   </tr>");
                }
                
              //上线数据详情
              
              $('#online_osspath').text(filePathSplit(data[i].ossPath));  
              $('#online_version').text(result.online.vision);
              $('#online_operTime').text(result.online.operTime);
              $('#online_upTime').text(result.online.upTime);
              
            }
   
        },
        error : function() {
            alert("异常！");
        }
    });
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
