<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  
            <head>
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <meta name="description" content="Xenon Boostrap Admin Panel">
                <meta name="author" content="">
                
                <title>Xenon - File Upload</title>
            
                <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Arimo:400,700,400italic">
                <link rel="stylesheet" href="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/css/fonts/linecons/css/linecons.css">
                <link rel="stylesheet" href="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/css/fonts/fontawesome/css/font-awesome.min.css">
                <link rel="stylesheet" href="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/css/bootstrap.css">
                <link rel="stylesheet" href="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/css/xenon-core.css">
                <link rel="stylesheet" href="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/css/xenon-forms.css">
                <link rel="stylesheet" href="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/css/xenon-components.css">
                <link rel="stylesheet" href="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/css/xenon-skins.css">
                <link rel="stylesheet" href="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/css/custom.css">

            
                <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
                <!--[if lt IE 9]>
                    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
                    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
                <![endif]-->
                <script src="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/js/jquery-1.11.1.min.js"></script>
                
            </head>
 
    <body>
            <div class="panel panel-default">
			
                    <div class="panel-heading">
                        <div class="panel-title">
                            文件上传
                        </div>
                    </div>
                    
                    <div class="panel-body">
                        
                        <form method="POST" action="/uploadFile" class="dropzone dz-clickable" id="uploadForm" enctype="multipart/form-data">
                            <div class="dz-default dz-message" style="background: none;">
                                <h1 style="text-align: center;color: #000">点击上传文件(仅支持ipa,apk)</h1>
                                <span>把文件放到这里上传</span></div>
                        </form>
                    </div>
                </div>

                <link rel="stylesheet" href="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/js/dropzone/css/dropzone.css">
                <script src="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/js/bootstrap.min.js"></script>
                <script src="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/js/TweenMax.min.js"></script>
                <script src="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/js/resizeable.js"></script>
                <script src="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/js/joinable.js"></script>
                <script src="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/js/xenon-api.js"></script>
                <script src="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/js/xenon-toggles.js"></script>
                <script src="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/js/dropzone/dropzone.min.js"></script>
                <script src="http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/assets/js/xenon-custom.js"></script>
                <script src="static/js/jquery.form.js"></script>
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