package org.ramanandi.framework.presentation;

import javax.validation.Valid;

import org.ramanandi.framework.domain.model.jpa.BaseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.szczytowski.genericdao.impl.GenericDao;

public abstract class BaseCrudController<T extends BaseEntity> {
	
	protected GenericDao<T, String> _dao;
	private Class<T> _entityClass;

	public BaseCrudController(Class<T> entityClass) {
		_entityClass = entityClass;
		
	}
	
	public GenericDao<T, String> getDao() {
		return _dao;
	}

	public void setDao(GenericDao<T, String> dao) {
		_dao = dao;
	}

	@ModelAttribute("entity")
	public T prepare(@RequestParam(value="id", required=false) String id) {
		if(!StringUtils.hasText(id)) {
			return _dao.create();
		}
		return _dao.get(id);
	}
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}	
	
	@RequestMapping(method=RequestMethod.GET)
	public String list(Model model) {
		model.addAttribute("list", _dao.getAll());
		return _entityClass.getSimpleName()+"-list";
	}
	
	@RequestMapping(value="/create", method = RequestMethod.GET)
	public String create() {
		return _entityClass.getSimpleName()+"-crud";
	}
	
	@RequestMapping(value="/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("entity") @Valid  T entity, BindingResult result) {
		if (result.hasErrors()) {
			return _entityClass.getSimpleName()+"-crud";
		}		
		onSave(entity);
		return "redirect:";
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	public String delete(@ModelAttribute("entity") T entity) {
		onDelete(entity);
		return "redirect:"+_entityClass.getSimpleName().toLowerCase();
	}
	
	protected void onSave(T entity){
		_dao.save(entity);
	}
	
	protected void onDelete(T entity) {
		_dao.delete(entity);
	}
	
}
