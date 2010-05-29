package com.glittle.alarm.presentation;

import org.ramanandi.framework.presentation.BaseCrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.glittle.alarm.domain.model.Alarm;
import com.glittle.alarm.infrastructure.persistence.jpa.AlarmDao;

@Controller
@RequestMapping("/alarm/")
public class AlarmController extends BaseCrudController<Alarm>{

	public AlarmController() {
		super(Alarm.class);
	}
	
	@Autowired
	public void setDao(AlarmDao dao) {
		super.setDao(dao);
	}
}
