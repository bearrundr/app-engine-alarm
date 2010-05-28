package org.ramanandi.framework.domain.model.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.datanucleus.jpa.annotations.Extension;

import com.szczytowski.genericdao.api.IEntity;

@Entity
@MappedSuperclass
public abstract class BaseEntity implements IEntity<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
	private String _id;	
	
	public BaseEntity() {}
	
	
	public String getId() {		
		return _id;
	}

	public void setId(String id) {
		_id = id;
	}

	public boolean isNew() {
		return _id == null;
	}
}
