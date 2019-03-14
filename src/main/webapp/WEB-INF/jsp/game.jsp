<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>游戏管理</title>
    <link rel="stylesheet" href="../static/css/table_manage.css">
    <link rel="stylesheet" href="../static/css/bootstrap.css">
    <style>
    	.xt-table td, .xt-table th {
		    padding: 6px 4px;
		}
		.xt-table th{text-align:center}
    </style>
</head>
<body>
	
	<h1 style="text-align:center;padding:5px;margin:20px">游戏管理</h1>
        <div id="xt-right" style="height: 827px;">
            
            <div class="xt-input">
                <input type="button" value="添加" class="green-int" onclick="add()">
                
            </div>
            <div class="xt-table">
                <table cellpadding="0" cellspacing="0" border="0" bgcolor="#dcdcdc" width="100%" id="mytb">
                	<thead>
                		<tr>
						<th>游戏ID</th>
						<th>游戏名称</th>
						<th>状态</th>
						<th>mq_channel</th>
						<th>对应oss</th>
						<th>操作</th>
						</tr>
                	</thead>
                    <tbody>
                    
                	</tbody>
                </table>
            </div>
        </div>
        
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="myModalLabel">新增</h4>
                </div>
                <div class="modal-body">

                    <div class="form-group">
                        <label for="txt_departmentname">游戏ID</label>
                        <input type="text" name="gameId" class="form-control" id="gameId" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="txt_departmentname">游戏名称</label>
                        <input type="text" name="name" class="form-control" id="name" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="txt_parentdepartment">状态</label>
                        <select class="form-control input-sm" id="open" name="open">
							<option value="0">关闭</option>
							<option value="1">开启</option>
					    </select>
                    </div>
                    <div class="form-group">
                        <label for="txt_departmentlevel">mq_channel</label>
                        <input type="text" name="channel" class="form-control" id="channel" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="txt_statu">对应oss</label>
                        <select class="form-control input-sm" id="oss" name="oss">
                        	<option value="0">空</option>
						</select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" onclick="save()" class="btn btn-primary" data-dismiss="modal">保存</button>
                </div>
            </div>
        </div>
    </div>

	<script src="../static/js/jquery-1.11.1.min.js"></script>
	<script src="../static/js/bootstrap.min.js"></script>
	<script src="../static/js/bootstrap-table.js"></script>
	<script>
		window.onload = function() {
			ossInit();
			init();
		}
		
		var ossData;
		function ossInit() {
			$.ajax({
				type : "POST",//方法类型
				async: false,
				dataType : "json",//预期服务器返回的数据类型
				contentType : "application/json; charset=utf-8",
				url : "/oss/connects",//url
				data : "",
				success : function(result) {
					//console.log(result);
					if (result.code == 0) {
						ossData = result.data;
						$("#oss").empty();
						$("#oss").append("<option value='0'>空</option>");
						for (var i = 0; i < ossData.length; i++) {
							var obj = ossData[i];
							$("#oss").append("<option value='"+obj.id+"'>"+obj.detail+"</option>");
						}
					} else {
						alert(result.message);
					}
				},
				error : function() {
					alert("异常！");
				}
			});
		}
		
		function toStr(open) {
			if (open == 0) {
				return "关闭";
			}
			return "开启";
		}
		
		function toString(ossId) {
			if (ossId == 0) {
				return "空";
			}
			for (var i = 0; i < ossData.length; i++) {
				var obj = ossData[i];
				if (obj.id == ossId) {
					return obj.detail;
				}
			}
			return "underfind";
		}
		
		function init() {
			$.ajax({
				type : "POST",//方法类型
				async: false,
				dataType : "json",//预期服务器返回的数据类型
				contentType : "application/json; charset=utf-8",
				url : "/game/list",//url
				data : "",
				success : function(result) {
					//console.log(result);
					if (result.code == 0) {
						$("#mytb tbody").empty();
						var tb = $("#mytb tbody");
						var arry = result.data;
						var trHTML = "";
						for (var i = 0; i < arry.length; i++) {
							var obj = arry[i];
							trHTML += "<tr>"
									+ "<td class='gameId'>"+ obj.gameId+"</td>"
									+ "<td class='name'>"+ obj.name+ "</td>"
									+ "<td>"+ toStr(obj.open)+ "</td>"
									+ "<input type='hidden'  value='"+obj.open+"' class='open'>"
									+ "<td class='channel'>"+ obj.channel+ "</td>"
									+ "<td>"+ toString(obj.ossId)+ "</td>"
									+ "<input type='hidden'  value='"+obj.ossId+"' class='ossId'>"
									+ "<td><a class='blue-xt btn_update'>修改</a></td>"
									+ "</tr>";
						}
						tb.append(trHTML);
					} else {
						alert(result.message);
					}
				},
					error : function() {
						alert("异常！");
					}
				});
		}
		
		var url ="";
		function save() {
			var data = '{"gameId":"'+$('#gameId').val()+'","name":"'+$('#name').val()+
				'","open":"'+$('#open').val()+'","channel":"'+$('#channel').val()+'","ossId":"'+$('#oss').val()+'"}';
			console.log(data);
			$.ajax({
				type : "POST",//方法类型
				dataType : "json",//预期服务器返回的数据类型
				contentType : "application/json; charset=utf-8",
				url : url,//url
				data : data,
				success : function(result) {
					if (result.code == 0) {
						init();
					} else {
						alert(result.message);
					}
				},
				error : function() {
					alert("异常！");
				}
			});
		}	
		//add
		function add() {
			url = "/game/add";
			$("#myModalLabel").text("新增");
			$('#myModal').modal();
			$('#gameId').val("");
			$("#gameId").attr("readonly",false);
			$('#name').val("");
			$('#open').val("");
			$('#channel').val("");
			$('#oss').val("");
		}
		
		//update
		$(document).on('click','.btn_update',function() {
			url = "/game/update";
			$("#myModalLabel").text("更新");
			$('#myModal').modal();
			var gameId = $(this).parents('tr').find('.gameId').text();
			var name = $(this).parents('tr').find('.name').text();
			var open = $(this).parents('tr').find('.open').val();
			console.log(open);
			var channel = $(this).parents('tr').find('.channel').text();
			var ossId = $(this).parents('tr').find('.ossId').val();
			$('#gameId').val(gameId);
			$("#gameId").attr("readonly",true)
			$('#name').val(name);
			$('#channel').val(channel);
			$("#open option[value='"+open+"']").prop("selected","selected");
			$("#oss option[value='"+ossId+"']").prop("selected","selected");
		});
		
		
	</script>
</body>
</html>