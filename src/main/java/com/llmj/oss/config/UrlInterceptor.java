package com.llmj.oss.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.llmj.oss.util.StringUtil;

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
		
		String account = (String) request.getSession().getAttribute("account");
		if (StringUtil.isEmpty(account)) {
			response.sendRedirect(request.getContextPath()+"/");
			flag = false;
		} else {
			request.getSession().setAttribute("account", account);
		}
		
		return flag;
	}

	
}
