package com.glittle.alarm.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.glittle.alarm.infrastructure.persistence.jpa.UserDao;

@Controller
public class LoginController {
	private UserDao userDao;
	
	
	@Autowired
	public void setUserDao(UserDao dao) {
		this.userDao = dao;
	}
}
