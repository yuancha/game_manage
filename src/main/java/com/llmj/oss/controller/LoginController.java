package com.llmj.oss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.llmj.oss.config.RespCode;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.model.User;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
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
	
	@GetMapping("/login")
	public String home(Model model,HttpServletRequest request) {
		model.addAttribute("message", "欢迎登录");
		return "html/login";
	}
	
	@PostMapping("/login")
	@ResponseBody
	public RespEntity login(Model model,HttpServletRequest request,@RequestBody User user) {
		
		try {
			String name = user.getName();
			String pwd = user.getPwd();
			
			if (!name.equals("admin") || !pwd.equals("123")) {
				return new RespEntity(-2,"账号或密码错误");
			}
			
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			
		} catch (Exception e) {
			log.error("login error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	@RequestMapping("/home")
	public String goHome(Model model,HttpServletRequest request) {
		
		try {
			User user = (User) request.getSession().getAttribute("user");
			log.error("user -> name : {},pwd : {}",user.getName(),user.getPwd());
			model.addAttribute("message", "导航页面");
			return "html/home";
		} catch (Exception e) {
			log.error("goHome error,Exception -> {}",e);
		}
		return "html/error";
	}
	
	//登出操作 logout
}