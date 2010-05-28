package com.glittle.alarm.infrastructure.persistence.jpa;

import org.springframework.stereotype.Repository;

import com.glittle.alarm.domain.model.User;
import com.szczytowski.genericdao.impl.GenericDao;

@Repository("userDao")
public class UserDao extends GenericDao<User, String> {

}
