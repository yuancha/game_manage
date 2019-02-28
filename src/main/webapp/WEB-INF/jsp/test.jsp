<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>some test</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="ajax方式">
    <script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
    <script type="text/javascript">
        function test() {
        	var gameId = $("#gameId").val();
        	var gameType = $("#gameType").val();
        	var id = $("#id").val();
        	var data = '{"gameId":"'+gameId+'","gameType":"'+gameType+'","id":"'+id+'"}'
            $.ajax({
            //几个参数需要注意一下
                type: "POST",//方法类型
                dataType: "json",//预期服务器返回的数据类型
                contentType: "application/json; charset=utf-8",
                url: "/oss/copyFile" ,//url
                data: data,
                success: function (result) {
                    console.log(result);//打印服务端返回的数据(调试用)
                    if (result.code == 0) {
                        alert("SUCCESS!啦啦啦");
                    }
                    ;
                },
                error : function() {
                    alert("异常！");
                }
            });
        }
    </script>
</head>
<body>
<div id="form-div">
	<%-- <%@include file = "nav.jsp" %> --%>
    <form id="form1" onsubmit="return false" action="##" method="post">
    	<p>id：<input name="id" type="number" id="id" tabindex="1" size="15" value=""/></p>
        <p>游戏id：<input name="gameId" type="number" id="gameId" tabindex="1" size="15" value="65537"/></p>
        <p>游戏类型：<input name="gameType" type="number" id="gameType" tabindex="2" size="16" value="0"/></p>
        <p>游戏状态：<input name="gameState" type="number" id="gameState" tabindex="2" size="16" value="${gameState}"/></p>
        <p><input type="button" value="测试" onclick="test()">&nbsp;<input type="reset" value="重置"></p>
    </form>
</div>
</body>
</html>