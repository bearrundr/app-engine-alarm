package com.glittle.alarm.presentation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.glittle.alarm.domain.model.Alarm;
import com.glittle.alarm.domain.model.User;
import com.glittle.alarm.infrastructure.persistence.jpa.AlarmDao;
import com.glittle.alarm.infrastructure.persistence.jpa.UserDao;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Controller
@RequestMapping("/alarm/")
public class AlarmController {

	private static final Logger LOGGER = Logger.getLogger(AlarmController.class);
	private static final String HOME_PAGE = "home";
	
	private UserService userService = UserServiceFactory.getUserService();
	private AlarmDao alarmDao;
	private UserDao userDao;
	
	public AlarmController() {	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String home(Model model) {
		List<Alarm> alarms = getCurrentUser().getAlarms();
		model.addAttribute("alarms", alarms);		
		return HOME_PAGE;
	}
	
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public String create(@RequestParam("time") String time) throws ParseException {
		System.out.println(time);
		Date alarmTime = new SimpleDateFormat("kk:mm").parse(time);				
		User user = getCurrentUser();
		Alarm alarm = new Alarm();
		alarm.setTime(alarmTime);
		user.addAlarm(alarm);
		userDao.save(user);
		return null;
	}
	
	@Autowired
	public void setAlarmDao(AlarmDao dao) {
		this.alarmDao = dao;
	}
	
	@Autowired
	public void setUserDao(UserDao dao) {
		this.userDao = dao;
	}
	
	public User getCurrentUser() {
		return userDao.findByUserId(userService.getCurrentUser().getUserId());
	}
}
