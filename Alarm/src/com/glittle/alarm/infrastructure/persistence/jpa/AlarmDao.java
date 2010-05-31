package com.glittle.alarm.infrastructure.persistence.jpa;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.glittle.alarm.domain.model.Alarm;
import com.glittle.alarm.domain.model.User;
import com.szczytowski.genericdao.impl.GenericDao;

@Repository("alarmDao")
public class AlarmDao extends GenericDao<Alarm, String> {

	public List<Alarm> findByUserId(String userId) {
		Query q = getEntityManager().createQuery("select alarms from "+User.class.getName()+" u where u.userId=?1");
		q.setParameter(1, userId);
		List<Alarm> list = q.getResultList();
		return list;
	}
	
	public List<Alarm> findByUserEmail() {
		return null;
	}
}
