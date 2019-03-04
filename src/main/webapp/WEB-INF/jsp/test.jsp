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
        	var desc = $("#desc").val();
        	var android = $("#android").val();
        	var ios = $("#ios").val();
        	var data = '{"gameId":"'+gameId+'","desc":"'+desc+'","android":"'+android+'","ios":"'+ios+'"}'
            $.ajax({
            //几个参数需要注意一下
                type: "POST",//方法类型
                dataType: "json",//预期服务器返回的数据类型
                contentType: "application/json; charset=utf-8",
                url: "/package/list" ,//url
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
    	<p>游戏id：<input name="gameId" type="number" id="gameId" tabindex="1" size="15" value=""/></p>
        <p>描述：<input name="desc" type="text" id="desc" tabindex="1" size="15" value=""/></p>
        <p>安卓包名：<input name="android" type="text" id="android" tabindex="2" size="16" value=""/></p>
        <p>ios包名：<input name="ios" type="text" id="ios" tabindex="2" size="16" value=""/></p>
        <p><input type="button" value="测试" onclick="test()">&nbsp;<input type="reset" value="重置"></p>
    </form>
</div>
</body>
</html>