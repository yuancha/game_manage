package com.llmj.oss.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.llmj.oss.model.User;

public class UrlInterceptor implements HandlerInterceptor {
	
	private GlobalConfig global;
	
	public UrlInterceptor(GlobalConfig global) {
		this.global = global;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean flag = true;
		String url = request.getRequestURI();
		System.out.println("请求路径，"+url);
		if (global.notIntercept(url)) {
			return flag;
		}
		
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			response.sendRedirect(request.getContextPath()+"/login");
			flag = false;
		} else {
			request.getSession().setAttribute("user", user);
			request.getSession().setAttribute("account", user.getName());
		}
		
		return flag;
	}

	
}
