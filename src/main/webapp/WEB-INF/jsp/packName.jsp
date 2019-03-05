<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>APP包名管理</title>
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
	
	<h1 style="text-align:center;padding:5px;margin:20px">APP包名管理</h1>
        <div id="xt-right" style="height: 827px;">
            
            <div class="xt-input">
                <!-- <span>账号类型</span>
                <select class="int-text">
	                <option>选项一</option>
	                <option>选项二</option>
	                <option>选项三</option>
	                <option>选项四</option>
	                <option>选项五</option>
	            </select> -->
                <input type="button" value="添加" class="green-int" onclick="add()">
                
            </div>
            <div class="xt-table">
                <table cellpadding="0" cellspacing="0" border="0" bgcolor="#dcdcdc" width="100%" id="mytb">
                	<thead>
                		<tr>
						<th>游戏ID</th>
						<th>游戏描述</th>
						<th>android</th>
						<th>iOS</th>
						<th>操作</th>
						</tr>
                	</thead>
                    <tbody>
                    
                	</tbody>
                </table>
            </div>
            <!-- <div class="xt-fenye">
                <div class="xt-fenye-left">当前第 1 / 270 页,每页10条，共 2696条记录</div>
                <div class="xt-fenye-right">
                    <a href="#">首页</a>
                    <a href="#">上一页</a>
                    <a href="#">下一页</a>
                    <a href="#">尾页</a>
                    <input type="text" name="text">
                    <a href="#" class="xt-link">跳转</a>
                </div>
            </div> -->
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
                        <label for="txt_parentdepartment">游戏描述</label>
                        <input type="text" name="content" class="form-control" id="content" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="txt_departmentlevel">android</label>
                        <input type="text" name="android" class="form-control" id="android" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="txt_statu">ios</label>
                        <input type="text" name="ios" class="form-control" id="ios" placeholder="">
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
			init();
		}
		function init() {
			$.ajax({
				type : "POST",//方法类型
				dataType : "json",//预期服务器返回的数据类型
				contentType : "application/json; charset=utf-8",
				url : "/package/list",//url
				data : "",
				success : function(result) {
					console.log(result);
					if (result.code == 0) {
						$("#mytb tbody").empty();
						var tb = $("#mytb tbody");
						var arry = result.data;
						var trHTML = "";
						for (var i = 0; i < arry.length; i++) {
							var obj = arry[i];
							trHTML += "<tr>"
									+ "<td class='gameId'>"+ obj.gameId+"</td>"
									+ "<td class='content'>"+ obj.content+ "</td>"
									+ "<td class='android'>"+ obj.android+ "</td>"
									+ "<td class='ios'>"+ obj.ios+ "</td>"
									+ "<td><a class='blue-xt btn_update'>修改</a><a class='blue-xt btn_del'>删除</a></td>"
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
			var data = '{"gameId":"'+$('#gameId').val()+'","content":"'+$('#content').val()+
				'","android":"'+$('#android').val()+'","ios":"'+$('#ios').val()+'"}';
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
			url = "/package/add";
			$("#myModalLabel").text("新增");
			$('#myModal').modal();
			$('#gameId').val("");
			$('#content').val("");
			$('#android').val("");
			$('#ios').val("");
		}
		
		//update
		$(document).on('click','.btn_update',function() {
			url = "/package/update";
			$("#myModalLabel").text("更新");
			$('#myModal').modal();
			var gameId = $(this).parents('tr').find('.gameId').text();
			var content = $(this).parents('tr').find('.content').text();
			var android = $(this).parents('tr').find('.android').text();
			var ios = $(this).parents('tr').find('.ios').text();
			$('#gameId').val(gameId);
			$('#content').val(content);
			$('#android').val(android);
			$('#ios').val(ios);
		});
		
		//del
		$(document).on('click','.btn_del',function() {
			var gameId = $(this).parents('tr').find('.gameId').text();
			var data = '{"gameId":"'+gameId+'"}'
			$.ajax({
				type : "POST",//方法类型
				dataType : "json",//预期服务器返回的数据类型
				contentType : "application/json; charset=utf-8",
				url : "/package/del",//url
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
		});
	</script>
</body>
</html>