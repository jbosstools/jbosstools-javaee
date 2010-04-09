package org.jboss.jsr299.tck.tests.jbt.validation.resources;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.xml.ws.WebServiceRef;

public class ProducerFieldsBroken {

	@Named
	@Produces @Resource //(lookup="java:global/env/jdbc/CustomerDatasource")
	Object customerDatabaseResourceBroken;

	@Named("service")
	@Produces @WebServiceRef(lookup="java:app/service/PaymentService")
	PaymentService paymentServiceResourceBroken;

	@Named
	@Produces @EJB //(ejbLink="../their.jar#PaymentService")
	PaymentService paymentServiceEjbResourceBroken;

	@Named
	@Produces @PersistenceContext(unitName="CustomerDatabase")
	EntityManager customerDatabasePersistenceContextResourceBroken;

	@Named @Produces @PersistenceUnit(unitName="CustomerDatabase")
	EntityManagerFactory customerDatabasePersistenceUnitResourceBroken;
}