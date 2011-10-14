package test;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.solder.core.ExtensionManaged;

public class EntityManagerTest {

	@ExtensionManaged
	@Produces
	@MyQualifier 
	EntityManagerFactory managerFactory;
	
	
	@Inject
	@MyQualifier 
	EntityManager manager;

	@ExtensionManaged
	@Produces
	SessionFactory sessionFactory;

	@Inject
	Session session;
	
}
