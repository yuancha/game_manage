
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!doctype html>
<html lang="en"><head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="Xenon Boostrap Admin Panel">
        <meta name="author" content="">
        <title>六六游戏集装箱</title>
        <link rel="stylesheet" href="../static/css/bootstrap.css">
        <link rel="stylesheet" href="../static/css/xenon/xenon-core.css">
        <link rel="stylesheet" href="../static/css/xenon/xenon-forms.css">
        <link rel="stylesheet" href="../static/css/xenon/xenon-components.css">
        <link rel="stylesheet" href="../static/css/xenon/xenon-skins.css">
        <script src="../static/js/jquery-1.11.1.min.js"></script>
        <link rel="stylesheet" href="../static/css/main.css">
        <style>
        	@media screen and (max-width: 768px){
        		#qr_box td img{width:50px;}
        		.col-md-12{padding:0}
        		#qrtb thead tr>th:nth-child(3),#qrtb tbody tr>td:nth-child(3){display:none} 
        	}
        	@media screen and (min-width: 768px){
        		#qr_box td img{width:100px;}
        	}
        </style>
    </head>
    <body class="page-body">
        <div class="page-container">
        	<%@include file ='nav.jsp'%>
			<input type="hidden" id="gameId">
            <div class="main-content">
                <nav class="navbar user-info-navbar" role="navigation">
                    <ul class="user-info-menu left-links list-inline list-unstyled">
                        <li class="hidden-sm hidden-xs" style="min-height: 76px;">
                            <a href="#" data-toggle="sidebar">
                                <i class="fa-bars"></i>
                            </a>
                        </li>
                    </ul>
                    <ul class="user-info-menu right-links list-inline list-unstyled">
                        <li class="search-form" style="min-height: 76px;">
                            <form method="get" action="extra-search.html">
                                <input type="text" name="s" class="form-control search-field" placeholder="搜索...">
                                <button type="submit" class="btn btn-link">
                                    <i class="linecons-search"></i>
                                </button>
                            </form>
                        </li>
                        
                        <li class="dropdown user-profile" style="min-height: 76px;">
                            <a href="#" data-toggle="dropdown">
                                <img src="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/images/user-4.png" alt="user-image" class="img-circle img-inline userpic-32" width="28">
                                <span>${account}
                                    <i class="fa-angle-down"></i>
                                </span>
                            </a>
                            <ul class="dropdown-menu user-profile-menu list-unstyled">
                                <li>
                                    <a href="#edit-profile">
                                        <i class="fa-edit"></i>
                                       	 未命名
                                    </a>
                                </li>
                                <li>
                                    <a href="#help">
                                        <i class="fa-info"></i>
                                      	 帮助
                                    </a>
                                </li>
                                <li class="last">
                                    <a href="extra-lockscreen.html">
                                        <i class="fa-lock"></i>
                                                                                 退出
                                    </a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </nav>
                
                <div class="page-title">
                    <div class="title-env">
                        <h1 class="title"><span id="showState"></span><input type="hidden" value="${gameState}" id="app_state"></h1>
                    </div>
                    <div class="breadcrumb-env">
                        <ol class="breadcrumb bc-1">
                           <li>
                              <a href="#"><i class="fa-home"></i>六六游戏</a>
                           </li>
                           <li class="active">
                              <a href="#">内蒙</a>
                           </li>
                         </ol>
                   	</div>
                </div>
                
                <div class="row">
                    <div class="col-md-12">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                               	 二维码信息
                               	 <a title="创建"  rel="创建"  href="javascript:;" onclick="modalShow();" >
                                 <button class="btn btn-secondary" id='qrcode_create'>创建</button></a>
                            </div>
                            <table class="table" style="text-align: center" id="qrtb">
                                <thead>
                                    <tr>
                                    	<th width="25%"  style="text-align: center">二维码</th>
                                        <th width="40%"  style="text-align: center">链接</th>
                                        <th width="20%"  style="text-align: center">描述</th>
                                        <th style="text-align: center">操作</th>
                                    </tr>
                                </thead>
                                <tbody id='qr_box'>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                
                  <div class="row">
	                    <div class="col-md-6">
	                    	<div class="panel-heading">
                               	 线上包信息
                            </div>
	                   		<div class="panel panel-default">
	                   			<p>线上名称：<span id="online_osspath"></span></p>
	                    		<p>上传名称：<span id="online_version"></span></p>
	                    		<p>上传时间：<span id="online_operTime"></span></p>
	                    		<p>操作时间：<span id="online_upTime"></span></p>
	                    	</div>
	                    </div>
	                    <div class="col-md-6">
	                    	<div class="panel-heading">
                               	 上传操作
                            </div>
	                   		<div class="panel panel-default">
	                   			<form id="form1" action="/uploadFile" target="frame1" method="post" enctype="multipart/form-data">
        							<input type="file" name="file">
        							<input type="hidden" name="upstate" value="" id="upstate">
        							<br>
        							<input type="button" value="上传" onclick="upload()">
							    </form>
							    <iframe name="frame1" frameborder="0" height="40" width="500"></iframe>
	                    	</div>
	                    </div>
                  </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="panel panel-default" style="overflow-y: scroll;overflow-x: hidden;">
                            <div class="panel-heading">
                               	 游戏包列表
                            </div>
                            <input type='hidden' id='app_system' value=''>
                            <p class="description" id="wukongvip">
                            	<button class="btn btn-secondary" id='app_android'>Android</button>&nbsp;&nbsp;&nbsp;&nbsp;<span id="pnandroid">安卓包名</span>&nbsp;&nbsp;&nbsp;&nbsp;
                            	<button class="btn btn-gray" id='app_iOS'>iOS</button>&nbsp;&nbsp;&nbsp;&nbsp;<span id="pnios">ios包名</span>&nbsp;&nbsp;&nbsp;&nbsp;
                        	</p>
                            <table class="table" style="text-align: center">
                                <thead>
                                    <tr>
                                    	<th width="30%"  style="text-align: center">游戏包名称</th>
                                        <th width="30%"  style="text-align: center">文件名</th>
                                        <th width="20%"  style="text-align: center">备注</th>
                                        <th width="10%"  style="text-align: center">状态</th>
                                        <th style="text-align: center">操作</th>
                                    </tr>
                                </thead>
                                <tbody id='app_box' style = "height: 300px;">
                                	
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
             	
                <footer class="main-footer sticky footer-type-1" style="margin-top: 120px;">
                    <div class="footer-inner">
                        <div class="footer-text">
                            © 2019 
                            <strong>六六游戏</strong> 
                             <a href="#" target="_blank" title="游戏集装箱">游戏集装箱</a> 北京承海网络科技有限公司 
                        </div>
                        <div class="go-up">
                            <a href="#" rel="go-top">
                                <i class="fa-angle-up"></i>
                            </a>
                        </div>
                    </div>
                </footer>
            </div>
            
        </div>
        
        <!-- model -->
        <div class="modal fade" id="modal-qr" data-backdrop="static">
	            <div class="modal-dialog">
	                <div class="modal-content">
	                    
	                    <div class="modal-header">
	                        <h4 class="modal-title">新建二维码</h4>
	                    </div>
	                    
	                    <div class="modal-body">
	                    	<div class="form-horizontal">
			                    	<div class="form-group">
			                    		<label class="col-sm-4 control-label">域名:</label>
			                    		<div class="col-sm-6">
											<select class="form-control" id="domain">
											</select>
										</div>
			                    	</div>
			                    	<div class="form-group">
			                    		<label class="col-sm-4 control-label">中间是否带图标:</label>
			                    		<div class="col-sm-6">
											<select class="form-control" id="middelImg">
												<option value="0">不带</option>
												<option value="1">带</option>
											</select>
										</div>
			                    	</div>
		                    		<div class="form-group-separator"></div>
			                         <div class="form-group">   	
			                            <label class="col-sm-4 control-label">描述:</label>		
			                        	<div class="col-sm-8">
			                            	<textarea  class="form-control" placeholder="请填写描述，不能超过50字" style="min-height:100px;" id="desc"></textarea>
			                           </div>
			                        </div>
		                        	<div class="form-group-separator"></div>
		                    	
		                   </div>
	                    <div class="modal-footer">
	                    	<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
	                        <button type="button" class="btn btn-info" data-dismiss="modal"  id="app_confirm">确定</button>
	                    </div>
	                </div>
	            </div>
	        </div>
        </div>
    	
    	<div class="modal fade" id="modal-app" data-backdrop="static">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title">文件详情</h4>
                    </div>
                    <div class="modal-body">
                           	 消息框内容
                    </div>
                    <div class="modal-footer" id="btnShow">
                    	<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
                    	<button type="button" class="btn btn-secondary" data-dismiss="modal" id="app_down">下载</button>
                        <button type="button" class="btn btn-danger" data-dismiss="modal" id="app_delete">删除</button>
                        <!-- <button type="button" class="btn btn-info" data-dismiss="modal" id="app_copy">发布</button> -->
                    </div>
                </div>
            </div>
        </div>
    	
    	<div class="modal fade custom-width" id="modal-qr-info">
            <div class="modal-dialog" style="width: 60%;">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                        <h4 class="modal-title">二维码详情</h4>
                    </div>
                    <div class="modal-body">
                        	消息框内容
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-white" data-dismiss="modal" id='btn_close_1'>关闭</button>
                        <button type="button" class="btn btn-danger" id="btn_del">删除</button>
                    </div>
                </div>
            </div>
        </div>
    	
        <!-- Bottom Scripts -->
        <script src="../static/js/bootstrap.min.js"></script>
        <script src="../static/js/xenon/TweenMax.min.js"></script>
        <script src="../static/js/xenon/resizeable.js"></script>
        <script src="../static/js/xenon/joinable.js"></script>
        <script src="../static/js/xenon/xenon-api.js"></script>
        <script src="../static/js/xenon/xenon-toggles.js"></script>
    
    
        <!-- JavaScripts initializations and stuff -->
        <script src="../static/js/xenon/xenon-custom.js"></script>
        <script src="../static/js/public.js?v=2"></script>
    	<script src="../static/js/app_function.js?v=6"></script>
    	<script src="../static/js/qrcode_function.js?v=1"></script>
 		<script>
 		$(document).ready(function(){
 			var gameId 		= $('#main-menu').find('li:nth-child(1)>a').attr('id');
 			var name 		= $('#main-menu').find('li:nth-child(1)>a').find('span').text();
 			if (!gameId) {
 				return;
 			}
 			var gameType 	= "0";
 			var gameState 	= $('#app_state').val();
 			var data 		= '{"gameId":"'+gameId+'","gameType":"'+gameType+'","gameState":"'+gameState+'"}';
 			$('#gameId').val(gameId);
 			$('.page-title').find('.active').find('a').html(name);
 			getFilesInfoByAjax(data);
 			if (gameState == 0) {//测试数据
 				$("#btnShow").append('<button type="button" class="btn btn-info" data-dismiss="modal" id="app_copy">发布</button>');	
 			}
 			
 			//二维码
 			getDomain();
 			var qrdata = '{"gameId":"'+gameId+'","state":"'+gameState+'"}';
 	    	getListByAjax(qrdata);
 	    	
 	    	//上传
 	    	$('#upstate').val(gameState);
 	    	
 	    	//包名
 	    	getPackName(gameId);
 	    	
 	    	//悟空vip链接
 	    	if (gameState == 1) {//正式数据
 				getVipLink(gameId);
 			}
 		})
 		 
 		</script>
		
		<script>
			function modalShow() {
		    	$('#modal-qr').modal();
		    	$("#desc").val("");
		    }
		</script>
		
		<script type="text/javascript">
	        function upload() {
	            $("#form1").submit();
	            var t = setInterval(function() {
	                //获取iframe标签里body元素里的文字。即服务器响应过来的"上传成功"或"上传失败"
	                var word = $("iframe[name='frame1']").contents().find("body").text();
	                if (word != "") {
	                    //alert(word);        //弹窗提示是否上传成功
	                    clearInterval(t);   //清除定时器
	                    /* var gameId  = $('#gameId').val();
	                	var gameType = $('#app_system').val();
	                	var gameState 	= $('#app_state').val();
	                	var data 		= '{"gameId":"'+gameId+'","gameType":"'+gameType+'","gameState":"'+gameState+'"}';
	                	getFilesInfoByAjax(data); */
	                }
	            }, 1000);
	        }
    	</script>
		
		
</body></html>