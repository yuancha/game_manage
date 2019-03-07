
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!doctype html>
<html lang="en"><head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="Xenon Boostrap Admin Panel">
        <meta name="author" content="">
        
        <title>游戏二维码管理</title>
    
      
        
        <link rel="stylesheet" href="../static/css/bootstrap.css">
        <link rel="stylesheet" href="../static/css/xenon/xenon-core.css">
        <link rel="stylesheet" href="../static/css/xenon/xenon-forms.css">
        <link rel="stylesheet" href="../static/css/xenon/xenon-components.css">
        <link rel="stylesheet" href="../static/css/xenon/xenon-skins.css">
     
    
        <script src="../static/js/jquery-1.11.1.min.js"></script>
        <link rel="stylesheet" href="../static/css/main.css">
    </head>
    <body class="page-body">
  
        <div class="page-container">
        	<%@include file ='nav.jsp'%>
			<input type="hidden" id="gameId">
            <div class="main-content" style="">
                        
                <!-- User Info, Notifications and Menu Bar -->
                <nav class="navbar user-info-navbar" role="navigation">
                    
                    <!-- Left links for user info navbar -->
                    <ul class="user-info-menu left-links list-inline list-unstyled">
                        
                        <li class="hidden-sm hidden-xs" style="min-height: 76px;">
                            <a href="#" data-toggle="sidebar">
                                <i class="fa-bars"></i>
                            </a>
                        </li>
                      
                        
                    </ul>
                    
                    
                    <!-- Right links for user info navbar -->
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
                                <span>
                                    袁超
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
                        <h1 class="title"><span id="showState"></span><input type="hidden" value="${gameState}" id="app_state">&nbsp;&nbsp;二维码管理</h1>
                       
                    </div>
                    
                        <div class="breadcrumb-env">
                        
                                    <ol class="breadcrumb bc-1">
                                        <li>
                                            <a href="#"><i class="fa-home"></i>六六游戏</a>
                                        </li>
                                        <li class="active">
                                            <a href="#">通辽</a>
                                        </li>
                                    </ol>
                                    
                    </div>
                        
                </div>
              
                <div class="row">
                    <div class="col-md-12">
                        
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                	二维码详情
                                	<a title="创建"  rel="创建"  href="javascript:;" onclick="jQuery('#modal-2').modal('show', {backdrop: 'fade'});" >
                                	<button class="btn btn-secondary" id='qrcode_create'>创建</button></a>
                            </div>
                            <table class="table" style="text-align: center" id="mytb">
                                <thead>
                                    <tr>
                                    	<th width="30%"  style="text-align: center">二维码</th>
                                        <th width="30%"  style="text-align: center">描述</th>
                                        <th width="30%"  style="text-align: center">链接</th>
                                        <th style="text-align: center">操作</th>
                                    </tr>
                                </thead>
                                <tbody id='app_box'>
                                	
                                </tbody>
                            </table>
                            
                        </div>
                    </div>
                </div>
               
                
             
                <footer class="main-footer sticky footer-type-1" style="margin-top: 120px;">
                    
                    <div class="footer-inner">
                    
                        <!-- Add your copyright text here -->
                        <div class="footer-text">
                            © 2019 
                            <strong>六六游戏</strong> 
                             <a href="#" target="_blank" title="游戏集装箱">游戏集装箱</a> 北京承海网络科技有限公司 
                        </div>
                        
                        
                        <!-- Go to Top Link, just add rel="go-top" to any link to add this functionality -->
                        <div class="go-up">
                        
                            <a href="#" rel="go-top">
                                <i class="fa-angle-up"></i>
                            </a>
                            
                        </div>
                        
                    </div>
                    
                </footer>
            </div>
        </div>
        
        
        
        <!-- Modal 2 (Custom Width)-->
        <div class="modal fade custom-width" id="modal-1">
            <div class="modal-dialog" style="width: 60%;">
                <div class="modal-content">
                    
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                        <h4 class="modal-title">消息框标题</h4>
                    </div>
                    
                    <div class="modal-body">
                        消息框内容
                        
                    </div>
                    
                    <div class="modal-footer">
                        <button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
                        <button type="button" class="btn btn-info">确定</button>
                    </div>
                </div>
            </div>
        </div>

        
        
        <!-- Modal 4 (Confirm)-->
        <div class="modal fade" id="modal-2" data-backdrop="static">
            <div class="modal-dialog">
                <div class="modal-content">
                    
                    <div class="modal-header">
                        <h4 class="modal-title">消息框标题</h4>
                    </div>
                    
                    <div class="modal-body">
                            	<p>请选择域名:
                            		<select style="width:300px;" id="domain">
                            			
                            		</select>
                            	</p>
                            	<p>请填写描述:<textarea placeholder="请填写描述，不能超过50字" style="width:300px;min-height:100px;" id="desc"></textarea></p>
                    </div>
                    
                    <div class="modal-footer">
                    	<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
                        <button type="button" class="btn btn-info" data-dismiss="modal" id="app_confirm">确定</button>
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
    	<script>
    		$(document).ready(function(){
    			getDomain();
    			getListByAjax();
    		});
    		
    		//确认创建
			$('#app_confirm').on('click',function(){
				var gameId = 666666;
    			var state = $("#app_state").val();
    			var domain = $("#domain").val();
    			var desc = $("#desc").val();
				var data = '{"gameId":"'+gameId+'","state":"'+state+'","domain":"'+domain+'","desc":"'+desc+'"}';
				createQrcode(data);
    		});
    		
    		function createQrcode(data){
	    			$.ajax({
						 //几个参数需要注意一下
				      type: "POST",//方法类型
				      dataType: "json",//预期服务器返回的数据类型
				      contentType: "application/json; charset=utf-8",
				      url: "/qrCode/add" ,//url
				      data: data,
				      success: function (result) {
				          if (result.code == 0) {
				        	  getListByAjax();
				              //alert("创建成功");    
				          }else{
				       	   	  alert(result.message);
				          }
				      },
				      error : function() {
				          alert("异常！");
				      }
				  });
    		}
    		
    		function getListByAjax(){
    			var gameId = 666666;
    			var state = $("#app_state").val();
    			var data = '{"gameId":"'+gameId+'","state":"'+state+'"}'
    			console.log(data);
    			$.ajax({
					 //几个参数需要注意一下
			      type: "POST",//方法类型
			      dataType: "json",//预期服务器返回的数据类型
			      contentType: "application/json; charset=utf-8",
			      url: "/qrCode/list" ,//url
			      data: data,
			      success: function (result) {
			          console.log(result);//打印服务端返回的数据(调试用)
			          if (result.code == 0) {
			        	  $("#mytb tbody").empty();
			        	   var tb = $("#mytb tbody");
							var arry = result.data;
							var trHTML = "";
							for (var i = 0; i < arry.length; i++) {
								var obj = arry[i];
								var bytes = obj.photo.split(",");
								var str = arrayBufferToBase64(bytes);
								trHTML += "<tr>"
										+ "<td><img style='width:100px;' src='data:image/png;base64,"+str+"'></td>"
										+ "<td class='content'>"+ obj.content+ "</td>"
										+ "<td class='link'>"+ obj.link+ "</td>"
										+ "<td><button class='btn_del'>删除</button></td>"
										+ "</tr>";
							}
							tb.append(trHTML);
			          }else{
			       	   	  alert(result.message);
			          }
			      },
			      error : function() {
			          alert("异常！");
			      }
			  });
    		}
    		
    		function getDomain(){
    			$.ajax({
					 //几个参数需要注意一下
			      type: "POST",//方法类型
			      dataType: "json",//预期服务器返回的数据类型
			      contentType: "application/json; charset=utf-8",
			      url: "/qrCode/domain" ,//url
			      data: "",
			      success: function (result) {
			          console.log(result);//打印服务端返回的数据(调试用)
			          if (result.code == 0) {
			        	  $("#domain").empty();
			        	  var ary = result.data;
			        	  for (var i=0;i<ary.length;i++) {
			        		  $("#domain").append("<option>"+ary[i].domain+"</option>");
			        	  }
			          }else{
			       	   	  alert(result.message);
			          }
			      },
			      error : function() {
			          alert("异常！");
			      }
			  });
    		}
    		
    		$(document).on('click','.btn_del',function() {
    			var link = $(this).parents('tr').find('.link').text();
    			var data = '{"domain":"'+link+'"}'
    			$.ajax({
    				type : "POST",//方法类型
    				dataType : "json",//预期服务器返回的数据类型
    				contentType : "application/json; charset=utf-8",
    				url : "/qrCode/del",//url
    				data : data,
    				success : function(result) {
    					if (result.code == 0) {
    						getListByAjax();
    					} else {
    						alert(result.message);
    					}
    				},
    				error : function() {
    					alert("异常！");
    				}
    			});
    		});
    		
    		function arrayBufferToBase64( buffer ) {
	    		var binary = '';
	    		var bytes = new Uint8Array( buffer );
	    		var len = bytes.byteLength;
	    		for (var i = 0; i < len; i++) {
    				binary += String.fromCharCode( bytes[ i ] );
    			}
    			return window.btoa( binary );
    		}
    	</script>
 

</body></html>