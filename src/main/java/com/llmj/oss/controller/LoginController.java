package com.llmj.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.llmj.oss.config.IConsts;
import com.llmj.oss.config.RespCode;
import com.llmj.oss.dao.UserDao;
import com.llmj.oss.model.Power;
import com.llmj.oss.model.RespEntity;
import com.llmj.oss.model.Role;
import com.llmj.oss.model.User;
import com.llmj.oss.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	
	private Map<String,HttpSession> sessionMap = new HashMap<>();	//account session
	private Lock loginLock = new ReentrantLock();
	
	@Autowired
	private UserDao userDao;
	
	@GetMapping("")
	public String home(Model model,HttpServletRequest request) {
		model.addAttribute("message", "欢迎登录");
		return "html/login";
	}
	
	@PostMapping("/login")
	@ResponseBody
	public RespEntity login(Model model,HttpServletRequest request,@RequestBody User user) {
		
		try {
			String account = user.getAccount();
			String pwd = user.getPassword();
			
			User my = userDao.selectUse(account, pwd);
			
			if (my == null) {
				return new RespEntity(-2,"账号或密码错误");
			}
			
			loginLock.lock();
			HttpSession session = request.getSession();
			String sessionId = session.getId();
			
			HttpSession oldSession = sessionMap.remove(account);
			if (oldSession != null && !oldSession.getId().equals(sessionId)) {
				//不是同一个session
				//oldSession.invalidate();
			}
			
			session.setAttribute("account", my.getAccount());
			sessionMap.put(account, session);
			userDao.updateLoginTime(my);
		} catch (Exception e) {
			log.error("login error,Exception -> {}",e);
			return new RespEntity(RespCode.SERVER_ERROR);
		} finally {
			loginLock.unlock();
		}
		return new RespEntity(RespCode.SUCCESS);
	}
	
	@RequestMapping("/home")
	public String goHome(Model model,HttpServletRequest request) {
		
		try {
			User user = (User) request.getSession().getAttribute("user");
			
			List<Power> powers = new ArrayList<>();
			if (user.getRole() == IConsts.RoleType.admin.getType()) {
				powers = userDao.selectPowers();
			} else {
				Role role = userDao.selectRole(user.getRole());
				String[] ary = role.getPower().split(",");
				for (String tmp : ary) {
					powers.add(userDao.selectPowersById(Integer.parseInt(tmp)));
				}
			}
			model.addAttribute("powers", StringUtil.objToJson(powers));
			return "html/home";
		} catch (Exception e) {
			log.error("goHome error,Exception -> {}",e);
		}
		return "html/error";
	}
	
	//登出操作 logout
}