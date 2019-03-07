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
		if (url.equals("/login") || url.equals("/upload") || url.equals("/uploadFile") || url.equals("/down/link") || url.equals("/error")) {
			return flag;
		}
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			response.sendRedirect(request.getContextPath()+"/login");
			flag = false;
		} else {
			request.getSession().setAttribute("user", user);
		}
		
		return flag;
	}

	
}
