package com.glittle.alarm.domain.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ramanandi.framework.domain.model.jpa.BaseEntity;

@Entity
public class Alarm extends BaseEntity {

	@Basic
	@Temporal(TemporalType.TIME)
	private Date time;

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	
}
