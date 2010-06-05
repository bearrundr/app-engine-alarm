package com.glittle.alarm.infrastructure.persistence.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

import com.glittle.alarm.domain.model.Alarm;
import com.glittle.alarm.domain.model.User;
import com.szczytowski.genericdao.impl.GenericDao;

@Repository("alarmDao")
public class AlarmDao extends GenericDao<Alarm, String> {

	private static final String FIND_BY_TIME = "select from " + Alarm.class.getName() + " alarm where alarm.time=?1";
	
	public List<Alarm> findByUserId(String userId) {
		Query q = getEntityManager().createQuery("select alarms from "+User.class.getName()+" u where u.userId=?1");
		q.setParameter(1, userId);
		List<Alarm> list = q.getResultList();
		return list;
	}
	
	public List<Alarm> findByTime(Date date) {
		Query q = getEntityManager().createQuery(FIND_BY_TIME);
		q.setParameter(1, date, TemporalType.TIME);
		List<Alarm> alarms = (List<Alarm>)q.getResultList();
		return alarms;
	}

}
