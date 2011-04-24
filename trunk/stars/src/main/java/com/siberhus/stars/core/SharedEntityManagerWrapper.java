package com.siberhus.stars.core;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import net.sourceforge.stripes.exception.StripesRuntimeException;

import com.siberhus.org.stripesstuff.stripersist.Stripersist;

/**
 * <p>
 * Because most {@link EntityManager} implementation which provided by many providers is not thread-safe.<br/>
 * It may cause the concurrency problem when Service/DAO class is singleton or may be shared in several thread.<br/>
 * To deal with this issue the {@link EntityManager} must be extracted from individual thread store.<br/>
 * And SharedEntityManagerWrapper is come in to play.<br/>
 * 
 * </p>
 * @author hussachai
 * @since 0.9
 * 
 */
public class SharedEntityManagerWrapper implements EntityManager {
	
	private static String defaultPersistenceUnit;
	
	private String persistenceUnit;
	
	public SharedEntityManagerWrapper(){}
	
	public SharedEntityManagerWrapper(String persistenceUnit){
		this.persistenceUnit = persistenceUnit;
	}
	
	private static EntityManager getEntityManager(String persistenceUnit) {
		if (persistenceUnit != null) {
			return Stripersist.getEntityManager(persistenceUnit);
		} else {
			return getEntityManager();
		}
	}
	
	private static EntityManager getEntityManager() {
		if (defaultPersistenceUnit == null) {
			return Stripersist.getEntityManager();
		}
		return Stripersist.getEntityManager(defaultPersistenceUnit);
	}
	
	private static EntityManagerFactory getEntityManagerFactory(
			String persistenceUnit) {
		if (persistenceUnit != null) {
			return Stripersist.getEntityManagerFactory(persistenceUnit);
		} else {
			return getEntityManagerFactory();
		}
	}

	private static EntityManagerFactory getEntityManagerFactory() {
		if (defaultPersistenceUnit == null) {
			throw new StripesRuntimeException(
					"In order to get EntityManagerFactory without persistenceUnitName you must"
							+ " set the value for Stripersitence.defaultPersistenceUnitName");
		}
		return Stripersist.getEntityManagerFactory(defaultPersistenceUnit);
	}
	
	@Override
	public void clear() {
		getEntityManager().clear();
	}

	@Override
	public void close() {
		getEntityManager(persistenceUnit).close();
	}
	
	@Override
	public boolean contains(Object entity) {
		return getEntityManager(persistenceUnit).contains(entity);
	}

	@Override
	public Query createNamedQuery(String name) {
		return getEntityManager(persistenceUnit).createNamedQuery(name);
	}
	
	@Override
	public Query createNativeQuery(String sqlString) {
		return getEntityManager(persistenceUnit).createNativeQuery(sqlString);
	}

	
	@Override
	public Query createNativeQuery(String sqlString, Class resultClass) {
		return getEntityManager(persistenceUnit).createNativeQuery(sqlString, resultClass);
	}
	
	@Override
	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		return getEntityManager(persistenceUnit).createNativeQuery(sqlString, resultSetMapping);
	}

	@Override
	public Query createQuery(String ejbqlString) {
		return getEntityManager(persistenceUnit).createQuery(ejbqlString);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		return getEntityManager(persistenceUnit).find(entityClass, primaryKey);
	}

	@Override
	public void flush() {
		getEntityManager(persistenceUnit).flush();
	}

	@Override
	public Object getDelegate() {
		return getEntityManager(persistenceUnit).getDelegate();
	}

	@Override
	public FlushModeType getFlushMode() {
		return getEntityManager(persistenceUnit).getFlushMode();
	}
	
	@Override
	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		return getEntityManager(persistenceUnit).getReference(entityClass, primaryKey);
	}

	@Override
	public EntityTransaction getTransaction() {
		return getEntityManager(persistenceUnit).getTransaction();
	}

	@Override
	public boolean isOpen() {
		return getEntityManager(persistenceUnit).isOpen();
	}

	@Override
	public void joinTransaction() {
		getEntityManager(persistenceUnit).joinTransaction();
	}

	@Override
	public void lock(Object entity, LockModeType lockMode) {
		getEntityManager(persistenceUnit).lock(entity, lockMode);
	}

	@Override
	public <T> T merge(T entity) {
		return getEntityManager(persistenceUnit).merge(entity);
	}

	@Override
	public void persist(Object entity) {
		getEntityManager(persistenceUnit).persist(entity);
	}

	@Override
	public void refresh(Object entity) {
		getEntityManager(persistenceUnit).refresh(entity);
	}

	@Override
	public void remove(Object entity) {
		getEntityManager(persistenceUnit).remove(entity);
	}

	@Override
	public void setFlushMode(FlushModeType flushMode) {
		getEntityManager(persistenceUnit).setFlushMode(flushMode);
	}
	
	
}





