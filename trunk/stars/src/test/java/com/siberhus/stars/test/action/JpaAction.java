package com.siberhus.stars.test.action;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;


public class JpaAction extends BaseAction {
	
	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@PersistenceUnit(unitName="movie-unit")
	private EntityManagerFactory movieEmf;
	
	@PersistenceContext(unitName="movie-unit")
	private EntityManager movieEm;
	
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public EntityManagerFactory getMovieEmf() {
		return movieEmf;
	}

	public EntityManager getMovieEm() {
		return movieEm;
	}
	
	
}
