package com.glittle.alarm.presentation;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.glittle.alarm.domain.model.Alarm;
import com.glittle.alarm.infrastructure.persistence.jpa.AlarmDao;
import com.glittle.alarm.infrastructure.persistence.jpa.UserDao;

@Controller
@RequestMapping("/cron")
public class CronController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CronController.class);
	private AlarmDao alarmDao;
	private UserDao userDao;
	
	public CronController() {}
	
	
	@RequestMapping(method=RequestMethod.POST)
	public void trigger(@RequestParam("alarmId") String alarmId) {
		Alarm alarm = alarmDao.get(alarmId);
		sendMail(alarm);
		alarmDao.delete(alarm);
	}
	
	
	@Autowired
	public void setAlarmDao(AlarmDao alarmDao) {
		this.alarmDao = alarmDao;
	}
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void sendMail(Alarm alarm) {
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "Alarm : "+alarm.getTime();

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("sramanandi@gmail.com", "Alarm service"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(alarm.getUser().getEmail(), alarm.getUser().getEmail()));
            msg.setSubject("Ring Ring Ring");
            msg.setText(msgBody);
            Transport.send(msg);
            
            LOGGER.info("Sent mail to "+alarm.getUser().getEmail());
            
        } catch(Exception e) {
        	throw new RuntimeException("Error when sending mail",e);        	
        }
	}
	
}
