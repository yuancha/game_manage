
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
    
        <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
            <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
        
         <link rel="stylesheet" href="../static/css/main.css">
    </head>
    <body class="page-body"><div id="BAIDU_DUP_fp_wrapper" style="position: absolute; left: -1px; bottom: -1px; z-index: 0; width: 0px; height: 0px; overflow: hidden; visibility: hidden; display: none;"><iframe id="BAIDU_DUP_fp_iframe" src="https://pos.baidu.com/wh/o.htm?ltr=" style="width: 0px; height: 0px; visibility: hidden; display: none;"></iframe></div>
    
    <script type="text/javascript">var cpro_id = "u837382";</script>
    <script src="http://cpro.baidustatic.com/cpro/ui/f.js" type="text/javascript"></script>
   
    
  
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
                        <h1 class="title"><span id="showState"></span><input type="hidden" value="${gameState}" id="app_state">&nbsp;&nbsp;游戏包</h1>
                        <p class="description">
                            <button class="btn btn-secondary" id='app_android'>Android</button>
                            <button class="btn btn-primary" id='app_iOS'>iOS</button>
                        </p>
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
	                    		<p>线上详情版本号：<span id="online_version"></span></p>
	                    		<p>上传时间：<span id="online_operTime"></span></p>
	                    		<p>操作时间：<span id="online_upTime"></span></p>
	                    		
	                    	</div>
	                    </div>
                  </div>
                <div class="row">
                    <div class="col-md-12">
                        
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                游戏包详情
                            </div>
                            <p>当前系统：<span id="app_system">Android</span></p>
                            <table class="table" style="text-align: center">
                                <thead>
                                    <tr>
                                    	<th width="30%"  style="text-align: center">游戏包名称</th>
                                        <th width="30%"  style="text-align: center">版本</th>
                                        <th style="text-align: center">操作</th>
                                    </tr>
                                </thead>
                                <tbody id='app_box'>
                                     <tr>
                                    	<td class="middle-align">111</td>
                                        <td class="middle-align">2019-2-25</td>
                                        <td>
                                            <a href="javascript:;" onclick="jQuery('#modal-1').modal('show', {backdrop: 'fade'});" class="btn btn-secondary btn-single btn-sm">查看</a>
                                            <a href="javascript:;" onclick="jQuery('#modal-2').modal('show', {backdrop: 'fade'});" class="btn btn-turquoise btn-single btn-sm">修改</a>
                                            <a href="#" class="btn btn-danger btn-single btn-sm">删除</a>
                                       
                                        </td>
                                    </tr>
                                    
                                    <tr>
                                    	<td class="middle-align">haha</td>
                                        <td class="middle-align">2019-2-25</td>
                                        <td>
                                            <a href="#" class="btn btn-secondary btn-single btn-sm">查看</a>
                                            <a href="#" class="btn btn-turquoise btn-single btn-sm">修改</a>
                                            <a href="#" class="btn btn-danger btn-single btn-sm">删除</a>
                                        </td>
                                    </tr>
                              
                                 
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
                            消息框内容
                    </div>
                    
                    <div class="modal-footer">
                    	<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
                        <button type="button" class="btn btn-danger" data-dismiss="modal" id="app_delete">删除</button>
                        <button type="button" class="btn btn-info" data-dismiss="modal" id="app_refresh">刷包</button>
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
    	<script src="../static/js/public_function.js?v=1"></script>
 

</body></html>