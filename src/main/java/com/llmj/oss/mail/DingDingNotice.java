package com.llmj.oss.mail;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.llmj.oss.util.HttpClientUtil;

@Service
public class DingDingNotice {
	
	public static String WEBHOOK_TOKEN = "https://oapi.dingtalk.com/robot/send?access_token=79a4b7f5ccf0be3358f9b86f5723b979bd520407c8986109769a75c415f6233a";
	 
    public void noticeGroup(String msg) {
        try {
        	String textMsg = "{ \"msgtype\": \"text\", \"text\": {\"content\": \""+msg+"\"}}";
            /*String result = HttpClientUtil.getInstance().sendHttpPost(WEBHOOK_TOKEN, textMsg);
            System.out.println("钉钉放回结果："+ result);*/
            HttpClient httpclient = HttpClients.createDefault();
            
            HttpPost httppost = new HttpPost(WEBHOOK_TOKEN);
            httppost.addHeader("Content-Type", "application/json; charset=utf-8");
     
            StringEntity se = new StringEntity(textMsg, "utf-8");
            httppost.setEntity(se);
     
            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                String result= EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(result);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
