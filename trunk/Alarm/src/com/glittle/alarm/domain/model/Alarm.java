package com.glittle.alarm.domain.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.ramanandi.framework.domain.model.jpa.BaseEntity;

@Entity
public class Alarm extends BaseEntity {

	@Basic
	@Temporal(TemporalType.DATE)
	private Date time;

	@Basic
	private User user;
	
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}	
	
	public long getSecondsForNextAlarm() {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		DateTime current = new DateTime(cal.getTime());		
		DateTime alarm = new DateTime(time);
		Interval interval = new Interval(current, alarm);
		Long milli = interval.toDurationMillis();
		
		return milli/1000;
	}
}
