<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>oss连接管理</title>
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
	
	<h1 style="text-align:center;padding:5px;margin:20px">oss连接管理</h1>
        <div id="xt-right" style="height: 827px;">
            
            <div class="xt-input">
                <input type="button" value="添加" class="green-int" onclick="add()">
                
            </div>
            <div class="xt-table">
                <table cellpadding="0" cellspacing="0" border="0" bgcolor="#dcdcdc" width="100%" id="mytb">
                	<thead>
                		<tr>
						<th>ID</th>
						<th>端点</th>
						<th>访问键ID</th>
						<th>访问密钥</th>
						<th>分支名称</th>
						<th>域名</th>
						<th>描述(选择标识)</th>
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
                        <label for="txt_departmentname">端点</label>
                        <input type="text" name="endpoint" class="form-control" id="endpoint" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="txt_departmentname">访问键ID</label>
                        <input type="text" name="accessKeyId" class="form-control" id="accessKeyId" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="txt_departmentname">访问密钥</label>
                        <input type="text" name="accessKeySecret" class="form-control" id="accessKeySecret" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="txt_departmentname">分支名称</label>
                        <input type="text" name="nbucketNameame" class="form-control" id="bucketName" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="txt_departmentname">域名</label>
                        <input type="text" name="domain" class="form-control" id="domain" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="txt_departmentlevel">描述(选择标识)</label>
                        <input type="text" name="detail" class="form-control" id="detail" placeholder="">
                    </div>
                    <input type="hidden" name="id" id="id" value="0" />
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
				url : "/oss/connects",//url
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
									+ "<td class='id'>"+ obj.id+"</td>"
									+ "<td class='endpoint'>"+ obj.endpoint+ "</td>"
									+ "<td class='accessKeyId'>"+ obj.accessKeyId+ "</td>"
									+ "<td class='accessKeySecret'>"+ obj.accessKeySecret+ "</td>"
									+ "<td class='bucketName'>"+ obj.bucketName+ "</td>"
									+ "<td class='domain'>"+ obj.domain+ "</td>"
									+ "<td class='detail'>"+ obj.detail+ "</td>"
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
		
		var url ="";
		function save() {
			var data = '{"endpoint":"'+$('#endpoint').val()+'","accessKeyId":"'+$('#accessKeyId').val()+
				'","accessKeySecret":"'+$('#accessKeySecret').val()+'","detail":"'+$('#detail').val()+'","id":"'+$('#id').val()+
				'","bucketName":"'+$('#bucketName').val()+'","domain":"'+$('#domain').val()+'"}';
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
			url = "/oss/add";
			$("#myModalLabel").text("新增");
			$('#myModal').modal();
			$('#endpoint').val("");
			$('#accessKeyId').val("");
			$('#accessKeySecret').val("");
			$('#bucketName').val("");
			$('#domain').val("");
			$('#detail').val("");
			$('#id').val("0");
		}
		
		//update
		$(document).on('click','.btn_update',function() {
			url = "/oss/update";
			$("#myModalLabel").text("更新");
			$('#myModal').modal();
			
			var id = $(this).parents('tr').find('.id').text();
			var endpoint = $(this).parents('tr').find('.endpoint').text();
			var accessKeyId = $(this).parents('tr').find('.accessKeyId').text();
			var accessKeySecret = $(this).parents('tr').find('.accessKeySecret').text();
			var bucketName = $(this).parents('tr').find('.bucketName').text();
			var domain = $(this).parents('tr').find('.domain').text();
			var detail = $(this).parents('tr').find('.detail').text();
			
			$('#endpoint').val(endpoint);
			$('#accessKeyId').val(accessKeyId);
			$('#accessKeySecret').val(accessKeySecret);
			$('#bucketName').val(bucketName);
			$('#domain').val(domain);
			$('#detail').val(detail);
			$('#id').val(id);
			
		});
		//del
		$(document).on('click','.btn_del',function() {
			if (window.confirm('确定删除吗？')) {
				var id = $(this).parents('tr').find('.id').text();
				var data = '{"id":"'+id+'"}'
				$.ajax({
					type : "POST",//方法类型
					dataType : "json",//预期服务器返回的数据类型
					contentType : "application/json; charset=utf-8",
					url : "/oss/del",//url
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