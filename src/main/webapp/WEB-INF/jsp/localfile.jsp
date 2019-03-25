<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>本地文件管理</title>
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
	
	<h1 style="text-align:center;padding:5px;margin:20px">本地文件管理</h1>
        <div id="xt-right" style="height: 827px;">
            
            <div class="xt-input">
                <span>游戏类型</span>
                <select class="int-text" id="gameselect">
	                
	            </select>
            </div>
            <div class="xt-table">
                <table cellpadding="0" cellspacing="0" border="0" bgcolor="#dcdcdc" width="100%" id="mytb">
                	<thead>
                		<tr>
						<th>文件名称</th>
						<th>上传名称</th>
						<th>测试状态</th>
						<th>正式状态</th>
						<th>操作</th>
						</tr>
                	</thead>
                    <tbody>
                    
                	</tbody>
                </table>
            </div>
            <div class="xt-fenye">
                <div class="xt-fenye-left" id="pageshow">当前第 1 / 270 页,每页10条，共 2696条记录</div>
                <div class="xt-fenye-right">
                    <a href="javascript:home()">首页</a>
                    <a href="javascript:previous()">上一页</a>
                    <a href="javascript:next()">下一页</a>
                    <a href="javascript:tail()">尾页</a>
                    <!-- <input type="text" name="text">
                    <a href="#" class="xt-link">跳转</a> -->
                </div>
            </div>
        </div>
        
	<script src="../static/js/jquery-1.11.1.min.js"></script>
	<script src="../static/js/bootstrap.min.js"></script>
	<script src="../static/js/bootstrap-table.js"></script>
	<script>
		
		var current = 1;
		var num = 0;
		var pagenum;
		var gameId;
		
		function pageinit() {
			var data = '{"gameId":"' + gameId + '"}';
			$.ajax({
					type : "POST",
					dataType : "json",
					async: false,
					contentType : "application/json; charset=utf-8",
					url : "/lofile/count",
					data : data,
					success : function(result) {
						if (result.code == 0) {
							num = result.data;
							pagenum = Math.ceil(num/10);
							current = 1;
							pageshow();
						} else {
							alert(result.message);
						}
					},
					error : function() {
						alert("异常！");
					}
			});
		}

		function pageshow() {
			var show = "当前第 " + current + " / " + pagenum + " 页,每页10条，共 " + num
					+ "条记录";
			$('#pageshow').text(show);
		}

		function previous() {
			if (current == 1) {
				alert("已经是第一页");
				return;
			}
			current -= 1;
			pageshow();
			init();
		}

		function next() {
			if (current == pagenum) {
				alert("已经是最后一页");
				return;
			}
			current += 1;
			pageshow();
			init();
		}

		window.onload = function() {
			gameInit();
			pageinit();
			init();
		}
		
		function gameInit() {
			$.ajax({
					type : "POST",
					dataType : "json",
					async: false,
					contentType : "application/json; charset=utf-8",
					url : "/game/list",
					data : "",
					success : function(result) {
						if (result.code == 0) {
							$("#gameselect").empty();
							var tb = $("#gameselect");
							var trHTML = "";
							var arry = result.data;
							for (var i = 0; i < arry.length; i++) {
								var obj = arry[i];
								trHTML += "<option value='"+obj.gameId+"'>"+obj.name+"</option>"
							}
							tb.append(trHTML);
							gameId = $('#gameselect option:first-child').val();
						} else {
							alert(result.message);
						}
					},
					error : function() {
						alert("异常！");
					}
			});
		}
		
		function init() {
			var data = '{"gameId":"' + gameId + '","page":"'+current+'"}';
			$.ajax({
				type : "POST",
				dataType : "json",
				async: false,
				contentType : "application/json; charset=utf-8",
				url : "/lofile/list",
				data : data,
				success : function(result) {
					//console.log(result);
					if (result.code == 0) {
						var arry = result.data;
						if (arry.length == 0 && current != 1) {
							//当前页为空 并且不是首页 返回首页
							pageinit();
							init();
							return;
						}
						$("#mytb tbody").empty();
						var tb = $("#mytb tbody");
						var trHTML = "";
						for (var i = 0; i < arry.length; i++) {
							var obj = arry[i];
							trHTML += "<tr>"
									+ "<td class='fileName'>"+ obj.fileName+"</td>"
									+ "<td>"+ obj.upName+ "</td>"
									+ "<td>"+ stateTostr(obj.testState)+ "</td>"
									+ "<td>"+ stateTostr(obj.onlineState)+ "</td>"
									+ "<td><a class='blue-xt btn_down'>下载</a><a class='red-xt btn_del'>删除</a></td>"
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
		
		function stateTostr(state) {
			var str = "";
			if (state == -1) {
				str = "无引用";
			} else if (state == 3) {
				str = "删除";
			} else if (state == 2) {
				str = "线上";
			} else {
				str = "普通";
			}
			return str;
		}
		
		//del
		$(document).on('click', '.btn_del', function() {

			if (window.confirm('确定删除吗？')) {

				var fileName = $(this).parents('tr').find('.fileName').text();
				var data = '{"gameId":"' + gameId + '","fileName":"'+fileName+'"}';
				$.ajax({
					type : "POST",
					dataType : "json",
					contentType : "application/json; charset=utf-8",
					url : "/lofile/del",//url
					data : data,
					success : function(result) {
						if (result.code == 0) {
							pageinit();
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
		
		//下载到本地
		$(document).on('click','.btn_down',function(){
			var fileName = $(this).parents('tr').find('.fileName').text();
			jQuery('<form action="/lofile/down" method="post">' +  // action请求路径及推送方法
		            '<input type="text" name="gameId" value="' + gameId + '"/>' + 
		            '<input type="text" name="fileName" value="' + fileName + '"/>' + 
		           '</form>').appendTo('body').submit().remove();
		});
		
		$("#gameselect").change(function(){
			gameId = $("#gameselect").val();
			pageinit();
			init();
		});
		
		function home() {
			alert("暂未实现");
		}
		
		function tail() {
			alert("暂未实现");
		}
	</script>
</body>
</html>