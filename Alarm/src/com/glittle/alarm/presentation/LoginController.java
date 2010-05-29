package com.glittle.alarm.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.glittle.alarm.infrastructure.persistence.jpa.UserDao;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Controller
@RequestMapping(value="/login/")
public class LoginController {
	
	private static final String LOGIN_PAGE = "login";
	
	private UserDao userDao;
	private UserService userService = UserServiceFactory.getUserService();
	
	@RequestMapping(method=RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("loginUrl", userService.createLoginURL("/app/login/success"));
		return LOGIN_PAGE;
	}
	
	@RequestMapping(value="/success", method=RequestMethod.GET)
	public String loginSuccess() {		
		User user = userService.getCurrentUser();		
		if(user != null) {
			
		}
		
		return null;
	}
	
	@Autowired
	public void setUserDao(UserDao dao) {
		this.userDao = dao;
	}
	
	private User toUser(User user) {		
		return null;
	}
}
