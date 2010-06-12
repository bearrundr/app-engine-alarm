package com.glittle.alarm.presentation;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.glittle.alarm.domain.model.Alarm;
import com.glittle.alarm.domain.model.User;
import com.glittle.alarm.infrastructure.persistence.jpa.AlarmDao;
import com.glittle.alarm.infrastructure.persistence.jpa.UserDao;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


@Controller
@RequestMapping("/alarm/")
public class AlarmController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlarmController.class);
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
	
	@Transactional(propagation=Propagation.REQUIRED)
	@RequestMapping(value="/create", method=RequestMethod.POST)		
	public ModelAndView create(@RequestParam("time") String time) throws ParseException {
		System.out.println(time);		
		DateFormat dt = new SimpleDateFormat("MM/dd/yyyy kk:mm");
		dt.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date alarmTime = dt.parse(time);				
		User user = getCurrentUser();
		Alarm alarm = new Alarm();
		alarm.setTime(alarmTime);
		user.addAlarm(alarm);
		
		userDao.save(user);	
		userDao.flush();
		
		
		
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(url("/app/cron").countdownMillis(
				getTimeDifference(alarmTime)).param("alarmId", alarm.getId()));
		
		JSONObject json = new JSONObject();
		try {
			json.put("time", alarm.getSecondsForNextAlarm());
			json.put("id", alarm.getId());
		} catch (JSONException e) {			
			LOGGER.error(e.getMessage());
		}
		return new ModelAndView("jsonview", "root", json);
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public ModelAndView delete(@RequestParam("alarmId") String id) {		
		JSONObject json = new JSONObject();
		try {
			alarmDao.delete(id);
			json.put("status", "success");
		} catch (JSONException e) {
			LOGGER.error(e.getMessage());			
		}
		return  new ModelAndView("jsonview", "root", json);
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
	
	private long getTimeDifference(Date dt) {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		Date current = cal.getTime();
		long difference = dt.getTime() - current.getTime();
		return difference;
	}
}
