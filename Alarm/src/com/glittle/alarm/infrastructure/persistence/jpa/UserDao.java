package com.glittle.alarm.infrastructure.persistence.jpa;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.glittle.alarm.domain.model.User;
import com.szczytowski.genericdao.impl.GenericDao;

@Repository("userDao")
public class UserDao extends GenericDao<User, String> {

	public User findByUserId(String userId) {
		Query q = getEntityManager().createQuery("select from "+User.class.getName()+" user where user.userId = ?1");
		q.setParameter(1, userId);
		User u = (User)q.getSingleResult();
		return u;
	}
}
