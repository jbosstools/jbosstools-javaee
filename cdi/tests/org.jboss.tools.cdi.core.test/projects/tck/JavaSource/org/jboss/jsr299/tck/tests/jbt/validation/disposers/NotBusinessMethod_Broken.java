package org.jboss.jsr299.tck.tests.jbt.validation.disposers;

import javax.ejb.Stateful;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateful
public class NotBusinessMethod_Broken implements LocalInt {
	@PersistenceContext	EntityManager em;

	@Produces
	public EntityManager retrieveEntityManager() {
		return em;
	}

	public void disposeEntityManager(@Disposes EntityManager em) {
	}
}