//主导航条
$('#main-menu').find('a').on('click',function(){
	$(this).parents('li').addClass('active').siblings("li").removeClass('active');
	
	var gameId 		= $(this).attr('id');
	var gameType 	= "0";
	var gameState 	= $('#app_state').val();
	var data 		= '{"gameId":"'+gameId+'","gameType":"'+gameType+'","gameState":"'+gameState+'"}';
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
	        	  getListByAjax();
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
	console.log(data);
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
					var bytes = obj.photo.split(",");
					var str = arrayBufferToBase64(bytes);
					trHTML += "<tr>"
							+ "<td style='vertical-align: middle;'><img src='data:image/png;base64,"+str+"'></td>"
							+ "<td style='vertical-align: middle;' class='content'>"+ obj.content+ "</td>"
							+ "<td style='vertical-align: middle;' class='link'>"+ obj.link+ "</td>"
							+ "<td style='vertical-align: middle;'><button class='btn btn-danger btn-single btn-sm btn_del'>删除</button></td>"
							+ "</tr>";
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
       	   	  alert(result.message);
          }
      },
      error : function() {
          alert("异常！");
      }
  });
}



//删除
$(document).on('click','.btn_del',function() {
	var link = $(this).parents('tr').find('.link').text();
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