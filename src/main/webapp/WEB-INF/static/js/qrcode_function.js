//主导航条
$('#main-menu').find('a').on('click',function(){
	$(this).parents('li').addClass('active').siblings("li").removeClass('active');
	
	var gameId 		= $(this).attr('id');
	var state 	= $('#app_state').val();
	var data  = '{"gameId":"'+gameId+'","state":"'+state+'"}';
	$('#gameId').val(gameId);
	$('.page-title').find('.active').find('a').html(getNavTitle(gameId));
	getListByAjax(data); 
});
    	


//确认创建
$('#app_confirm').on('click',function(){
	var gameId  = $('#gameId').val();
	var state = $("#app_state").val();
	var domain = $("#domain").val();
	var desc = $("#desc").val();
	var data = '{"gameId":"'+gameId+'","state":"'+state+'","domain":"'+domain+'","desc":"'+desc+'"}';
	createQrcode(data);
});

function createQrcode(data){
		console.log(data);
		$.ajax({
			 //几个参数需要注意一下
	      type: "POST",//方法类型
	      dataType: "json",//预期服务器返回的数据类型
	      contentType: "application/json; charset=utf-8",
	      url: "/qrCode/add" ,//url
	      data: data,
	      success: function (result) {
	          if (result.code == 0) {
	        	  var gameId  = $('#gameId').val();
	        	  var state = $("#app_state").val();
	        	  var data1 = '{"gameId":"'+gameId+'","state":"'+state+'"}';
	        	  getListByAjax(data1);
	        	  alert(result.message);
	              //alert("创建成功");    
	          }else{
	       	   	  alert(result.message);
	          }
	      },
	      error : function() {
	          alert("异常！");
	      }
	  });
}

function getListByAjax(data){
//	var gameId  = $('#gameId').val();
//	var state = $("#app_state").val();
//	var data = '{"gameId":"'+gameId+'","state":"'+state+'"}'
	$.ajax({
		 //几个参数需要注意一下
      type: "POST",//方法类型
      dataType: "json",//预期服务器返回的数据类型
      contentType: "application/json; charset=utf-8",
      url: "/qrCode/list" ,//url
      data: data,
      success: function (result) {
          console.log(result);//打印服务端返回的数据(调试用)
          if (result.code == 0) {
        	  $("#mytb tbody").empty();
        	   var tb = $("#mytb tbody");
				var arry = result.data;
				var trHTML = "";
				for (var i = 0; i < arry.length; i++) {
					var obj = arry[i];
					//var bytes = obj.photo.split(",");
					//var str = arrayBufferToBase64(bytes);
					trHTML += "<tr>\
							<td style='vertical-align: middle;'><img src='"+obj.ossPath+"'></td>\
							<td style='vertical-align: middle;' class='content'>"+ obj.content+ "</td>\
							<td style='vertical-align: middle;'>\
							<button class='btn btn-secondary btn-single btn-sm btn_look'>查看</button>\
							<button class='btn btn-turquoise btn-single btn-sm btn_refresh'>刷新</button>\
							</td>\
							<input type='hidden' class='link' value='"+ obj.link+ "'>\
							<input type='hidden' class='id' value='"+ obj.id+ "'>\
							<input type='hidden' class='state' value='"+ obj.state+ "'>\
							<input type='hidden' class='logicUse' value='"+ obj.logicUse+ "'>\
							<input type='hidden' class='gameId' value='"+ obj.gameId+ "'>\
							</tr>";
				}
				tb.append(trHTML);
          }else{
       	   	  alert(result.message);
          }
      },
      error : function() {
          alert("异常！");
      }
  });
}

function getDomain(){
	$.ajax({
		 //几个参数需要注意一下
      type: "POST",//方法类型
      dataType: "json",//预期服务器返回的数据类型
      contentType: "application/json; charset=utf-8",
      url: "/qrCode/domain" ,//url
      data: "",
      success: function (result) {
          console.log(result);//打印服务端返回的数据(调试用)
          if (result.code == 0) {
        	  $("#domain").empty();
        	  var ary = result.data;
        	  for (var i=0;i<ary.length;i++) {
        		  $("#domain").append("<option>"+ary[i].domain+"</option>");
        	  }
          }else{
       	   	  alert("getDomain" + result.message);
          }
      },
      error : function() {
          alert("异常！");
      }
  });
}



//查看
$(document).on('click','.btn_look',function() {
	$('#modal-1').modal('show', {backdrop: 'fade'});
	//$('#modal-1').find('.modal-body').children().remove();
	var qrcode_id 		= $(this).parents('tr').find('.id').val();
	var qrcode_link 	= $(this).parents('tr').find('.link').val();
	var qrcode_state 	= $(this).parents('tr').find('.state').val();
	var qrcode_content 	= $(this).parents('tr').find('.content').text();
	var qrcode_logicUse = $(this).parents('tr').find('.logicUse').val();
	var qrcode_game_id 	= $(this).parents('tr').find('.gameId').val();

	
	$('#modal-1').find('.modal-body').html("\
			<p>游戏ID："+qrcode_game_id+"</p>\
			<p>链接："+qrcode_link+"</p>\
			<p>状态："+qrcode_state+"</p>\
			<p>描述："+qrcode_content+"</p>\
			<p>逻辑服使用："+qrcode_logicUse+"</p>\
			<input type='hidden' id='show_qrcode_link' value='"+qrcode_link+"'>\
	");
	

});


//删除
$(document).on('click','#btn_del',function() {
	
	var link = $(this).parents('.modal-content').find('#show_qrcode_link').val();
	var data = '{"domain":"'+link+'"}'
	$.ajax({
		type : "POST",//方法类型
		dataType : "json",//预期服务器返回的数据类型
		contentType : "application/json; charset=utf-8",
		url : "/qrCode/del",//url
		data : data,
		success : function(result) {
			if (result.code == 0) {
				$('#app_box').children().remove();
				var gameId  = $('#gameId').val();
				var state = $("#app_state").val();
				var data = '{"gameId":"'+gameId+'","state":"'+state+'"}';
				getListByAjax(data);
				alert(result.message);
				//window.location.reload();
				$('#btn_close_1').click();
			} else {
				alert(result.message);
			}
		},
		error : function() {
			alert("异常！");
		}
	});
});



//刷新
$(document).on('click','.btn_refresh',function() {
	
	var link = $(this).parents('tr').find('.link').val();
	var gameId  = $('#gameId').val();
	var state = $("#app_state").val();
	var data = '{"gameId":"'+gameId+'","domain":"'+link+'","state":"'+state+'"}';
	$.ajax({
		type : "POST",//方法类型
		dataType : "json",//预期服务器返回的数据类型
		contentType : "application/json; charset=utf-8",
		url : "/qrCode/refresh",//url
		data : data,
		success : function(result) {
			if (result.code == 0) {
				$('#app_box').children().remove();
				var gameId  = $('#gameId').val();
				var state = $("#app_state").val();
				var data = '{"gameId":"'+gameId+'","state":"'+state+'"}';
				getListByAjax(data);
				alert(result.message);
				//window.location.reload();
			} else {
				alert(result.message);
			}
		},
		error : function() {
			alert("异常！");
		}
	});
	
});

function arrayBufferToBase64( buffer ) {
	var binary = '';
	var bytes = new Uint8Array( buffer );
	var len = bytes.byteLength;
	for (var i = 0; i < len; i++) {
		binary += String.fromCharCode( bytes[ i ] );
	}
	return window.btoa( binary );
}