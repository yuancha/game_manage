<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>域名管理</title>
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
	
	<h1 style="text-align:center;padding:5px;margin:20px">域名管理</h1>
        <div id="xt-right" style="height: 827px;">
            
            <div class="xt-input">
                <input type="button" value="添加" class="green-int" onclick="add()">
                
            </div>
            <div class="xt-table">
                <table cellpadding="0" cellspacing="0" border="0" bgcolor="#dcdcdc" width="100%" id="mytb">
                	<thead>
                		<tr>
						<th>域名</th>
						<th>描述</th>
						<th>状态</th>
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
                        <label for="txt_departmentname">域名</label>
                        <input type="text" name="domain" class="form-control" id="domain" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="txt_parentdepartment">描述</label>
                        <input type="text" name="content" class="form-control" id="content" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="txt_parentdepartment">状态</label>
                        <select class="form-control input-sm" id="type" name="type">
							<option value="0">启用</option>
							<option value="1">停用</option>
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
			init();
		}
		function init() {
			$.ajax({
				type : "POST",//方法类型
				dataType : "json",//预期服务器返回的数据类型
				contentType : "application/json; charset=utf-8",
				url : "/game/dm/list",//url
				data : "",
				success : function(result) {
					if (result.code == 0) {
						$("#mytb tbody").empty();
						var tb = $("#mytb tbody");
						var arry = result.data;
						var trHTML = "";
						for (var i = 0; i < arry.length; i++) {
							var obj = arry[i];
							trHTML += "<tr>"
									+ "<td class='domain'>"+ obj.domain+"</td>"
									+ "<td class='content'>"+ obj.content+ "</td>"
									+ "<td>"+typeToStr(obj.type)+ "</td>"
									+ "<input type='hidden'  value='"+obj.type+"' class='type'>"
									+ "<td><a class='blue-xt btn_update'>修改</a><a class='red-xt btn_del'>删除</a></td>"
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
		
		function typeToStr(type) {
			var show = "停用";
			if (type == 0) {
				show = "启动";
			}
			return show;
		}
		
		var url ="";
		function save() {
			var data = '{"domain":"'+$('#domain').val()+'","content":"'+$('#content').val()+'","type":"'+$('#type').val()+'"}';
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
			url = "/game/dm/add";
			$("#myModalLabel").text("新增");
			$('#myModal').modal();
			$('#domain').val("");
			$("#domain").attr("readonly",false);
			$('#content').val("");
			$("#type option[value='"+1+"']").prop("selected","selected");
		}
		
		//update
		$(document).on('click','.btn_update',function() {
			url = "/game/dm/update";
			$("#myModalLabel").text("更新");
			$('#myModal').modal();
			var domain = $(this).parents('tr').find('.domain').text();
			var content = $(this).parents('tr').find('.content').text();
			var type = $(this).parents('tr').find('.type').val();
			$('#domain').val(domain);
			$("#domain").attr("readonly",true);
			$('#content').val(content);
			$("#type option[value='"+type+"']").prop("selected","selected");
		});
		
		//del
		$(document).on('click','.btn_del',function() {
			if (window.confirm('确定删除吗？')) {
				var domain = $(this).parents('tr').find('.domain').text();
				var data = '{"domain":"'+domain+'"}';
				$.ajax({
					type : "POST",//方法类型
					dataType : "json",//预期服务器返回的数据类型
					contentType : "application/json; charset=utf-8",
					url : "/game/dm/del",//url
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
		});
	</script>
</body>
</html>