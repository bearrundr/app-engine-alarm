package com.glittle.alarm.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.glittle.alarm.domain.model.User;
import com.glittle.alarm.infrastructure.persistence.jpa.UserDao;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Controller
@RequestMapping(value="/login/")
public class LoginController {
	
	private static final String LOGIN_PAGE = "login";
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
	
	private UserDao userDao;
	private UserService userService = UserServiceFactory.getUserService();
	
	@RequestMapping(method=RequestMethod.GET)
	public String login(Model model) {
		if(userService.isUserLoggedIn()) {
			return "redirect:/app/alarm/";
		}
		model.addAttribute("loginUrl", userService.createLoginURL("/app/login/success"));
		return LOGIN_PAGE;
	}
	
	@RequestMapping(value="/success", method=RequestMethod.GET)
	public String loginSuccess() {		
		com.google.appengine.api.users.User apiUser = userService.getCurrentUser();		
		if(apiUser == null) {
			return "redirect:/app/login/";
		}
		if(apiUser != null) {
			User user = toUser(apiUser);
			if(userDao.findByUserId(user.getUserId()) == null) {
				userDao.save(user);
				LOGGER.info("User with email address "+ user.getEmail()+" is saved");				
			}
		}		
		return "redirect:/app/alarm/";
	}
	
	@Autowired
	public void setUserDao(UserDao dao) {
		this.userDao = dao;
	}
	
	private User toUser(com.google.appengine.api.users.User apiUser) {		
		User user = new User();
		user.setEmail(apiUser.getEmail());
		user.setUserId(apiUser.getUserId());
		return user;
	}
}
