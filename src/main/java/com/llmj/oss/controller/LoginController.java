package com.llmj.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.llmj.oss.dao.DownDao;
import com.llmj.oss.dao.UploadDao;
import com.llmj.oss.model.DownLink;
import com.llmj.oss.model.UploadFile;
import com.llmj.oss.model.User;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录管理
 * @author xinghehudong
 *
 */
@Controller
@Slf4j(topic = "ossLogger")
public class LoginController {
	
	/*@Autowired
	private UploadDao uploadDao;*/
	
	@PostMapping("/login")
	public String login(Model model,HttpServletRequest request) {
		
		try {
			String name = request.getParameter("name");
			String pwd = request.getParameter("pwd");
			
			if (!name.equals("admin") || !pwd.equals("123")) {
				model.addAttribute("message", "账号或密码错误");
				return "html/login";
			}
			
			HttpSession session = request.getSession();
			User user = new User();
			user.setName(name);
			user.setPwd(pwd);
			session.setAttribute("user", user);
			
			return "html/home";
		} catch (Exception e) {
			log.error("login error,Exception -> {}",e);
			model.addAttribute("message", "server error");
		}
		return "html/error";
	}
	
	//登出操作 logout
}