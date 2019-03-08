<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>游戏线上二维码</title>
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
	
	<h1 style="text-align:center;padding:5px;margin:20px">游戏线上二维码</h1>
        <div id="xt-right" style="height: 827px;">
            <div class="xt-table">
                <table cellpadding="0" cellspacing="0" border="0" bgcolor="#dcdcdc" width="100%" id="mytb">
                	<thead>
                		<tr>
						<th>二维码</th>
						<th>描述</th>
						<th>连接</th>
						</tr>
                	</thead>
                    <tbody>
                    
                	</tbody>
                </table>
            </div>
        </div>
        
	<script src="../static/js/jquery-1.11.1.min.js"></script>
	<script src="../static/js/bootstrap.min.js"></script>
	<script src="../static/js/bootstrap-table.js"></script>
	<script>
		window.onload = function() {
			var arry = JSON.parse('${qrs}');
			$("#mytb tbody").empty();
			var tb = $("#mytb tbody");
			var trHTML = "";
			for (var i = 0; i < arry.length; i++) {
				var obj = arry[i];
				trHTML += "<tr>"
						+ "<td><img src='"+obj.ossPath+"'  style='width: 100px; height: 100px;'/></td>"
						+ "<td>"+ obj.content+ "</td>"
						+ "<td>"+ obj.link+ "</td>"
						+ "</tr>";
			}
			tb.append(trHTML);
		}
	</script>
</body>
</html>