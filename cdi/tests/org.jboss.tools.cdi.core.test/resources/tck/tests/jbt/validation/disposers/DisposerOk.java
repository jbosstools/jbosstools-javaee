package org.jboss.jsr299.tck.tests.jbt.validation.disposers;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateful
@LocalBean
public class DisposerOk implements LocalInt {
	@PersistenceContext	EntityManager em;

	@Produces
	public EntityManager retrieveEntityManager() {
		return em;
	}

	public void disposeEntityManager(@Disposes EntityManager em) {
	}
}