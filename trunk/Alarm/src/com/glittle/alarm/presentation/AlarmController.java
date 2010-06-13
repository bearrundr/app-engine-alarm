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

import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	private static final DateFormat DATEFORMAT = new SimpleDateFormat("MM/dd/yyyy kk:mm");
	
	private UserService userService = UserServiceFactory.getUserService();
	private AlarmDao alarmDao;
	private UserDao userDao;
	
	
	static {
		DATEFORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public AlarmController() {	}
	
	@ModelAttribute
	public void prepare(Model model) {
		model.addAttribute("logoutUrl", userService.createLogoutURL("/"));
		model.addAttribute("userName", userService.getCurrentUser().getNickname());
	}
	
	
	@RequestMapping(method=RequestMethod.GET)
	public String home(Model model) {
		List<Alarm> alarms = getCurrentUser().getAlarms();
		model.addAttribute("alarms", alarms);		
		return HOME_PAGE;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@RequestMapping(value="/create", method=RequestMethod.POST)		
	public ModelAndView create(@RequestParam("time") String time) throws JSONException {
		System.out.println(time);
		JSONObject json = new JSONObject();
		User user = getCurrentUser();
		Date alarmTime = null;

		try {

			alarmTime = DATEFORMAT.parse(time);
			int differenceFromNow = dayDifferenceFromNow(alarmTime);
			if (differenceFromNow > 29) {
				json.put("error", "You can set alarm date max 30 days from now");
			} else if (differenceFromNow < 0) {
				json.put("error", "You can not set alarm in past");
			} else {

				Alarm alarm = new Alarm();
				alarm.setTime(alarmTime);
				user.addAlarm(alarm);

				userDao.save(user);
				userDao.flush();

				System.out.println("Difference " + dayDifferenceFromNow(alarmTime));

				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(url("/app/cron").countdownMillis(
						getTimeDifference(alarmTime)).param("alarmId",
						alarm.getId()));

				json.put("time", alarm.getSecondsForNextAlarm());
				json.put("id", alarm.getId());
			}
		} catch (ParseException e) {
			json.put("error", "You have entered date/time in invalid format.");			
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
	
	private int dayDifferenceFromNow(Date dt) {
		LocalDateTime now = new LocalDateTime(DateTimeZone.UTC);
		LocalDateTime alarm = new LocalDateTime(dt, DateTimeZone.UTC);
		if(alarm.isBefore(now)) return -1;
		return Days.daysBetween(now, alarm).getDays();
	}
}
