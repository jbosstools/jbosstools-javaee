package test;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Session;

public class EntityManager2Test {

	@Inject
	@MyQualifier
	EntityManager manager;

	@Inject
	Session session;
	
}
