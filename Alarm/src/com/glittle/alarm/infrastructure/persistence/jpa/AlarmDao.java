package com.glittle.alarm.infrastructure.persistence.jpa;

import org.springframework.stereotype.Repository;

import com.glittle.alarm.domain.model.Alarm;
import com.szczytowski.genericdao.impl.GenericDao;

@Repository("alarmDao")
public class AlarmDao extends GenericDao<Alarm, String> {

}
