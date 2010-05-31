package com.glittle.alarm.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.ramanandi.framework.domain.model.jpa.BaseEntity;

@Entity
public class User extends BaseEntity {
	
	@Basic
	private String email;
	
	@Basic
	private String userId;
	
	@OneToMany(mappedBy="user", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<Alarm> alarms;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}	
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<Alarm> getAlarms() {
		return alarms;
	}

	public void setAlarms(List<Alarm> alarms) {
		this.alarms = alarms;
	}	
	
	public void addAlarm(Alarm alarm) {
		if(this.alarms == null) {
			this.alarms = new ArrayList<Alarm>();
		}
		this.alarms.add(alarm);
	}
}
