package com.szczytowski.genericdao.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import com.szczytowski.genericdao.api.IActivable;
import com.szczytowski.genericdao.api.IDao;
import com.szczytowski.genericdao.api.IDefaultable;
import com.szczytowski.genericdao.api.IEntity;
import com.szczytowski.genericdao.api.IHiddenable;
import com.szczytowski.genericdao.api.IInheritable;
import com.szczytowski.genericdao.criteria.Criteria;
import com.szczytowski.genericdao.criteria.restriction.Restrictions;

/**
 * Abstract implementation of generic DAO.
 *
 * @param <T> entity type, it must implements at least <code>IEntity</code>
 * @param <I> entity's primary key, it must be serializable
 * @see IEntity
 * @author Maciej Szczytowski <mszczytowski-genericdao@gmail.com>
 * @since 1.0
 */
public class GenericDao<T extends IEntity<I>, I extends Serializable> implements IDao<T, I> {

    private EntityManager entityManager;

    private Class<IEntity<I>> clazz;

    private boolean isDefaultable;

    private boolean isActivable;

    private boolean isHiddenable;

    private boolean isInheritable;

    /**
     * Default constructor. Use for extend this class.
     */
    @SuppressWarnings(value = "unchecked")
    public GenericDao() {
        clazz = (Class<IEntity<I>>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        checkGenericClass();
    }

    public T create() {
    	T instance = null; 
    	try {
    		instance = (T)this.clazz.newInstance();
		} catch (InstantiationException e) {			
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return instance;
    }
    
    /**
     * Constructor with given {@link IEntity} implementation. Use for creting DAO without extending this class.
     *
     * @param clazz class with will be accessed by DAO methods
     */
    @SuppressWarnings(value = "unchecked")
    public GenericDao(Class<IEntity<I>> clazz) {
        this.clazz = clazz;
        checkGenericClass();
    }

    /**
     * Set entity manager.
     *
     * @param entityManager entity manager
     */
    @PersistenceContext
    public final void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public final T load(I id) throws EntityNotFoundException {
        T entity = get(id);
        if (entity == null) {
            throw new EntityNotFoundException("entity " + clazz + "#" + id + " was not found");
        }
        return entity;
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public final T get(I id) {
        return (T) entityManager.find(clazz, id);
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public final List<T> get(I... ids) {
        return findByCriteria(Criteria.forClass(clazz).add(Restrictions.in(IEntity.P_ID, ids)));
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public final List<T> get(IInheritable<T> parent) {
        if (parent == null) {
            return findByCriteria(Criteria.forClass(clazz).add(Restrictions.isNull(IInheritable.P_PARENT)));
        } else {
            return findByCriteria(Criteria.forClass(clazz).add(Restrictions.eq(IInheritable.P_PARENT, parent)));
        }
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public final List<T> getAll() {
        return findByCriteria(Criteria.forClass(clazz));
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public final List<T> findByExample(T example) {
        Criteria criteria = Criteria.forClass(clazz);

        Field[] fields = example.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getName().equals(IEntity.P_ID)) {
                continue;
            }
            if (field.getName().equals(IActivable.P_IS_ACTIVE)) {
                continue;
            }
            if (field.getName().equals(IDefaultable.P_IS_DEFAULT)) {
                continue;
            }
            if (field.getName().equals(IHiddenable.P_IS_HIDDEN)) {
                continue;
            }
            if (!field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Basic.class)) {
                continue;
            }

            Object value = null;

            try {
                field.setAccessible(true);
                value = field.get(example);
            } catch (IllegalArgumentException e) {
                continue;
            } catch (IllegalAccessException e) {
                continue;
            }

            if (value == null) {
                continue;
            }

            criteria.add(Restrictions.eq(field.getName(), value));
        }

        if (example instanceof IHiddenable) {
            if (((IInheritable) example).getParent() == null) {
                criteria.add(Restrictions.isNull(IInheritable.P_PARENT));
            } else {
                criteria.add(Restrictions.eq(IInheritable.P_PARENT, ((IInheritable) example).getParent()));
            }
        }

        return findByCriteria(criteria);
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public final void setAsDefault(IDefaultable object) {
        if (object.getExample() != null) {
            List<T> objects = findByExample((T) object.getExample());
            for (T o : objects) {
                if (object != o) {
                    ((IDefaultable) o).setDefault(false);
                    entityManager.merge(o);
                }
            }
        }
        object.setDefault(true);

        if (((T) object).getId() != null) {
            entityManager.merge(object);
        } else {
            entityManager.persist(object);
        }
    }

    @Override
    public final void save(final T object) {
        if (object.getId() != null) {
            entityManager.merge(object);
        } else {
            entityManager.persist(object);
        }
    }

    @Override
    public final void save(final T... objects) {
        for (T object : objects) {
            if (object.getId() != null) {
                entityManager.merge(object);
            } else {
                entityManager.persist(object);
            }
        }
    }

    @Override
    public final void delete(final I id) throws UnsupportedOperationException {
        delete(load(id));
    }

    @Override
    public final void delete(final I... ids) throws UnsupportedOperationException {
        deleteAll(get(ids), true);
    }

    @Override
    public final void delete(final T object) throws UnsupportedOperationException {
        if (isDefaultable) {
            checkIfDefault(object);
        }
        if (isHiddenable) {
            ((IHiddenable) object).setHidden(true);
            entityManager.merge(object);
        } else {
            entityManager.remove(object);
        }
    }

    @Override
    public final void delete(final T... objects) throws UnsupportedOperationException {
        deleteAll(Arrays.asList(objects), true);
    }

    @Override
    public final void deleteAll() throws UnsupportedOperationException {
        deleteAll(getAll(), false);
    }

    private final void deleteAll(final Collection<T> objects, boolean checkIdDefault) throws UnsupportedOperationException {
        if (checkIdDefault) {
            if (isDefaultable) {
                for (T object : objects) {
                    checkIfDefault(object);
                }
            }
        }
        if (isHiddenable) {
            for (T object : objects) {
                ((IHiddenable) object).setHidden(true);
                entityManager.merge(object);
            }
        } else {
            for (T object : objects) {
                entityManager.remove(object);
            }
        }
    }

    private final void checkIfDefault(T entity) {
        if (((IDefaultable) entity).isDefault()) {
            throw new UnsupportedOperationException("can't delete default entity " + clazz + "#" + entity.getId());
        }
    }

    private final void checkGenericClass() {
        for (Class i : clazz.getInterfaces()) {
            if (i == IDefaultable.class) {
                isDefaultable = true;
            } else if (i == IActivable.class) {
                isActivable = true;
            } else if (i == IHiddenable.class) {
                isHiddenable = true;
            } else if (i == IInheritable.class) {
                isInheritable = true;
            }
        }
    }

    @Override
    public final void refresh(final T entity) {
        entityManager.refresh(entity);
    }

    @Override
    public final void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * Retrieve objects using criteria. It is equivalent to <code>criteria.list(entityManager)</code>.
     *
     * @param criteria criteria which will be executed
     * @return list of founded objects
     * @see javax.persistence.Query#getResultList()
     */
    @SuppressWarnings(value = "unchecked")
    protected final List findByCriteria(Criteria criteria) {
        return criteria.list(entityManager);
    }

    /**
     * Retrieve an unique object using criteria. It is equivalent to <code>criteria.uniqueResult(entityManager)</code>.
     *
     * @param criteria criteria which will be executed
     * @return retrieved object
     * @throws NoResultException - if there is no result
     * @throws NonUniqueResultException - if more than one result
     * @see javax.persistence.Query#getSingleResult()
     */
    protected final Object findUniqueByCriteria(Criteria criteria) throws NonUniqueResultException, NoResultException {
        return criteria.uniqueResult(entityManager);
    }
    
    @Override
    public final boolean isActivable() {
        return isActivable;
    }

    @Override
    public final boolean isDefaultable() {
        return isDefaultable;
    }

    @Override
    public final boolean isHiddenable() {
        return isHiddenable;
    }

    @Override
    public final boolean isInheritable() {
        return isInheritable;
    }

    /**
     * Get entity manager.
     *
     * @return entity manager
     */
    protected final EntityManager getEntityManager() {
        return entityManager;
    }
}
