<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!doctype html>
<html>
  
            <head>
               <meta charset="utf-8">
		        <meta http-equiv="X-UA-Compatible" content="IE=edge">
		        
		        <meta name="viewport" content="width=device-width, initial-scale=1.0">
		        <meta name="description" content="Xenon Boostrap Admin Panel">
		        <meta name="author" content="">
        
                
                <title>文件上传</title>
            
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
 
    <body>
    
    	 <div class="page-container">
    		
    		<div class="panel panel-default">
                 <div class="panel-heading">
                     <div class="panel-title">文件上传</div>
                 </div>
                 
                 <div class="panel-body">
                     
                     <form method="POST" action="/uploadFile" class="dropzone dz-clickable" id="uploadForm" enctype="multipart/form-data">
                         <div class="dz-default dz-message" style="background: none;">
                             <h1 style="text-align: center;color: #000">点击上传文件(仅支持ipa,apk)</h1>
                             <span>把文件放到这里上传</span></div>
                     </form>
                 </div>
             </div>
    	</div>

                <link rel="stylesheet" href="../static/css/xenon/dropzone.css">
              
                 <!-- Bottom Scripts -->
		        <script src="../static/js/bootstrap.min.js"></script>
		        <script src="../static/js/xenon/TweenMax.min.js"></script>
		        <script src="../static/js/xenon/resizeable.js"></script>
		        <script src="../static/js/xenon/joinable.js"></script>
		        <script src="../static/js/xenon/xenon-api.js"></script>
		        <script src="../static/js/xenon/xenon-toggles.js"></script>
		    
		    
		        <!-- JavaScripts initializations and stuff -->
		        <script src="../static/js/xenon/xenon-custom.js"></script>
		    	<script src="../static/js/public.js?v=1"></script>
                <script>
                    /* $(function(){  
                       
                        $("#uploadForm").ajaxForm(function(data){    
                        	console.log(data);
                            var arr = JSON.parse(data);
                            var code = arr['code'];
                            console.log(code);
                            if(code=="0"){  
                                alert("提交成功！");     
                            }else{
                                alert(arr['message']);
                            }  
                        });       
                    });  */
                    $(document).ready(function() {
                    	
                      /*   $("#uploadForm").ajaxForm(function(data){
                              alert("返回的值是" + data);
                        });   */     
                        $('#uploadForm').on('click',function(){
                        	$(this).bind("change", function(){
                        		$(this).ajaxSubmit(function(data){
		                       			alert('提交成功！');
		                       			console.log(data);
	                       		});
                        	});
	 	                       		
	                  		
                        });
                       
                    });	
                </script>
    </body>
</html>