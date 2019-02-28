
$('#main-menu').find('a').on('click',function(){
	$(this).parents('li').addClass('active').siblings("li").removeClass('active');
	
	var gameId 		= $(this).attr('id');
	var gameType 	= "0";
	var data 		= '{"gameId":"'+gameId+'","gameType":"'+gameType+'"}';
	
	console.log(data);
	 $.ajax({
		 //几个参数需要注意一下
         type: "POST",//方法类型
         dataType: "json",//预期服务器返回的数据类型
         contentType: "application/json; charset=utf-8",
         url: "/oss/getFilesInfo" ,//url
         data: data,
         success: function (result) {
             console.log(result);//打印服务端返回的数据(调试用)
             if (result.code == 0) {
                 //alert("SUCCESS!啦啦啦");
                 var data = result.data;
                 $('#app_box').children().remove();
                 for(var i in data){
                
                	 $('#app_box').append("<tr>\
                		<td class='middle-align'>"+data[i].game+"</td>\
                        <td class='middle-align'>"+data[i].vision+"</td>\
                        <td>\
                            <a href='#' class='btn btn-secondary btn-single btn-sm ev_look'>查看</a>\
                            <a href='#' class='btn btn-turquoise btn-single btn-sm'>修改</a>\
                            <a href='#' class='btn btn-danger btn-single btn-sm'>删除</a>\
                        </td>\
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
             }
    
         },
         error : function() {
             alert("异常！");
         }
     });
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
	");
	
});



