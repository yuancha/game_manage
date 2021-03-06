
//首次进入区分正式服和测试服标识
/*if($('#app_state').val() == 0){
	$('#showState').text("测试服");
}else if($('#app_state').val() == 1){
	$('#showState').text("正式服");
}
*/

//主导航条
$('#main-menu').find('a').on('click',function(){
	$(this).parents('li').addClass('active').siblings("li").removeClass('active');
	
	var gameId 		= $(this).attr('id');
	var gameType 	= "0";
	$('#app_android').removeClass('btn-gray').siblings().removeClass('btn-secondary');
	$('#app_android').addClass('btn-secondary').siblings().addClass('btn-gray');
	var gameState 	= $('#app_state').val();
	var data 		= '{"gameId":"'+gameId+'","gameType":"'+gameType+'","gameState":"'+gameState+'"}';
	$('#gameId').val(gameId);
	var name 		= $(this).find('span').text();
	$('.page-title').find('.active').find('a').html(name);
	getFilesInfoByAjax(data); 
	getPackName(gameId);
	if (gameState == 1) {//正式数据
		getVipLink(gameId);
	}
});

function getPackName(gameId){
	var data = '{"gameId":"'+gameId+'"}';
	$.ajax({
      type: "POST",//方法类型
      dataType: "json",//预期服务器返回的数据类型
      contentType: "application/json; charset=utf-8",
      url: "/package/get" ,//url
      data: data,
      success: function (result) {
          if (result.code == 0) {
        	  var obj = result.data;
        	  $('#pnandroid').text(obj.android);
        	  $('#pnios').text(obj.ios);
          }else{
       	   	  alert(result.message);
          }
      },
      error : function() {
          alert("异常！");
      }
  });
}

$(document).on('click','.ev_look',function(){
	$('#modal-app').modal('show', {backdrop: 'fade'});
	$('#modal-app').find('.modal-body').children().remove();
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
	var up_name 	    = $(this).parents('tr').find('.fileName').val();

	if(app_type == 0){
		var type = "安卓";
	}else if(app_type == 1){
		var type = "苹果";
	}
	$('#modal-app').find('.modal-body').html("\
			<p>上传名称："+up_name+"</p>\
			<p>名称："+app_game+"</p>\
			<p>包名："+app_packName+"</p>\
			<p>版本："+app_vision+"</p>\
			<p>系统："+type+"</p>\
			<p>oss路径："+app_ossPath+"</p>\
			<p>本地路径："+app_localPath+"</p>\
			<p>更新时间："+app_operTime+"</p>\
			<input type='hidden' id='show_app_id' value='"+app_id+"'>\
	");
	
});



//游戏包系统切换

$('#app_android,#app_iOS').on('click',function(){
	$(this).removeClass('btn-gray').siblings().removeClass('btn-secondary');
	$(this).addClass('btn-secondary').siblings().addClass('btn-gray');

	
	var gameId  = $('#gameId').val();
	var gameType = $(this).attr('id');
	if(gameType == "app_android"){
		gameType   = "0";
		$('#app_system').val(0);
	}else if(gameType == "app_iOS"){
		gameType   = "1";
		$('#app_system').val(1);
	}
	var gameState = $('#app_state').val();
	var data 		= '{"gameId":"'+gameId+'","gameType":"'+gameType+'","gameState":"'+gameState+'"}';
	getFilesInfoByAjax(data); 
});


//刷包
$(document).on('click','.ev_refresh',function(){
	if (window.confirm('确定上线吗？')) {
		var id 			= $(this).parents('tr').find('.app_id').val();
		var gameState 	= $('#app_state').val();
		var data 		= '{"id":"'+id+'","gameState":"'+gameState+'"}';
		refreshFilesInfoByAjax(data); 
	}
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
          if (result.code == 0) {
              var gameId  = $('#gameId').val();
          	  var gameType = $('#app_system').val();
          	  var gameState 	= $('#app_state').val();
          	  var data 		= '{"gameId":"'+gameId+'","gameType":"'+gameType+'","gameState":"'+gameState+'"}';
          	  getFilesInfoByAjax(data);
          	  alert("操作成功"); 
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
$(document).on('click','#app_copy',function(){
	if (window.confirm('确定发布吗？')) {
		var id 			= $(this).parents('.modal-content').find('#show_app_id').val();
		var gameState 	= $('#app_state').val();
		var data 		= '{"id":"'+id+'","gameState":"'+gameState+'"}';
		copyFilesInfoByAjax(data); 
	}
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
           if (result.code == 0) {
               alert("发布成功");    
           }else {
        	   alert(result.message);
           }
       },
       error : function() {
           alert("异常！");
       }
   });
}

//删除
$(document).on('click','#app_delete',function(){
	if (window.confirm('确定删除吗？')) {
		var id 		= $(this).parents('.modal-content').find('#show_app_id').val();
		var gameState 	= $('#app_state').val();
		var data 		= '{"id":"'+id+'","gameState":"'+gameState+'"}';
		deleteFilesInfoByAjax(data); 
	}
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
              var gameId  = $('#gameId').val();
              var gameType = $('#app_system').val();
          	  var gameState 	= $('#app_state').val();
          	  var data 		= '{"gameId":"'+gameId+'","gameType":"'+gameType+'","gameState":"'+gameState+'"}';
          	  getFilesInfoByAjax(data);
          }else{
        	  alert(result.message);
          }
      },
      error : function() {
          alert("异常！");
      }
  });
}

//下载到本地
$(document).on('click','#app_down',function(){
	var id 		= $(this).parents('.modal-content').find('#show_app_id').val();
	var gameState 	= $('#app_state').val();
	jQuery('<form action="/down/file" method="post">' +  // action请求路径及推送方法
            '<input type="text" name="fileId" value="' + id + '"/>' + 
            '<input type="text" name="state" value="' + gameState + '"/>' + 
           '</form>').appendTo('body').submit().remove();
});

function stateToStr1(state) {
	if (state == 2) {
		return "线上";
	}
	return "";
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
                if (data.length == 0) return; 
                for(var i in data){
               	 $('#app_box').append("<tr>\
               		<td class='middle-align'>"+filePathSplit(data[i].localPath)+"</td>\
               		<td class='middle-align'>"+data[i].fileName+"</td>\
               		<td class='middle-align'><input type='text' value='"+data[i].notes+"' onblur='editNotes("+data[i].id+");' id='note_"+data[i].id+"'></td>\
                       <td class='middle-align'>"+stateToStr1(data[i].state)+"</td>\
                       <td>\
                           <a href='#' class='btn btn-secondary btn-single btn-sm ev_look'>查看</a>\
                           <a href='#' class='btn btn-turquoise btn-single btn-sm ev_refresh'>上线</a>\
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
                       <input type='hidden' value="+data[i].fileName+" class='fileName' >\
                   </tr>");
                }
                
              //上线数据详情
              if (result.online) {
            	  $('#online_osspath').text(filePathSplit(result.online.localPath));  
                  $('#online_version').text(result.online.vision);
                  $('#online_operTime').text(result.online.operTime);
                  $('#online_upTime').text(result.online.upTime);
              }  
            }
   
        },
        error : function() {
            alert("异常！");
        }
    });
}

function editNotes(id) {
	var noteid = "note_"+id;
	var notes = $("#"+noteid).val();
	var data = '{"id":"'+id+'","gameState":"'+$('#app_state').val()+'","notes":"'+notes+'"}';
	$.ajax({
		 //几个参数需要注意一下
     type: "POST",//方法类型
     dataType: "json",//预期服务器返回的数据类型
     contentType: "application/json; charset=utf-8",
     url: "/oss/notesUp" ,//url
     data: data,
     success: function (result) {
         if (result.code == 0) {
             var gameId  = $('#gameId').val();
             var gameType = $('#app_system').val();
         	  var gameState 	= $('#app_state').val();
         	  var data 		= '{"gameId":"'+gameId+'","gameType":"'+gameType+'","gameState":"'+gameState+'"}';
         	  getFilesInfoByAjax(data);
         }else{
       	  alert(result.message);
         }
     },
     error : function() {
         alert("异常！");
     }
	});
}
/*
 *	获得悟空vip链接 
 */
function getVipLink(gameId) {
	var data = "gameId="+gameId;
	$.ajax({
		 //几个参数需要注意一下
    type: "POST",//方法类型
    dataType: "json",//预期服务器返回的数据类型
    //contentType: "application/json; charset=utf-8",
    url: "/down/getVipLink" ,//url
    data: data,
    success: function (result) {
        if (result.code == 0) {
            var viplink = result.data;
            if($('#viplinkDiv').length > 0){
            	$("#viplink").val(viplink);
            }else{
            	$('#wukongvip').append("<div id='viplinkDiv' style='display:inline-block'><span style='color:#000;font-size:15px;'>悟空vip签名链接 </span>" +
                		"<input type='text' value='"+viplink+"' id='viplink' style='width:250px;'> " +
                				"<button onclick='upVipLink();'>保存</button></div>");
            }
        }else{
      	  alert(result.message);
        }
    },
    error : function() {
        alert("异常！");
    }
	});
}
/*
 * 更新悟空vip链接
 */
function upVipLink() {
	
	if (window.confirm('确定保存吗？')) {
		var data = "gameId="+$('#gameId').val()+"&link="+$('#viplink').val();
		$.ajax({
			 //几个参数需要注意一下
	    type: "POST",//方法类型
	    dataType: "json",//预期服务器返回的数据类型
	    //contentType: "application/json; charset=utf-8",
	    url: "/down/upVipLink" ,//url
	    data: data,
	    success: function (result) {
	        if (result.code == 0) {
	            
	        }else{
	      	  alert(result.message);
	        }
	    },
	    error : function() {
	        alert("异常！");
	    }
		});
	}
	
}
