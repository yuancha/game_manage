package com.llmj.oss.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.llmj.oss.model.User;

public class UrlInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean flag = true;
		String url = request.getRequestURI();
		System.out.println("请求路径，"+url);
		if (url.equals("/upload") || url.equals("/login") || url.equals("/uploadFile")) {
			return flag;
		}
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			flag = false;
		}
		
		return flag;
	}

	
}
