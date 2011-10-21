package org.jboss.defaultbean.producer;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.solder.bean.defaultbean.DefaultBean;

@DefaultBean(Test1.class)
public class Test1 {
	
	@Inject
	TypeB b;

	@Inject
	TypeA a;
	
	@Produces
	TypeA pa;

	@Produces
	TypeB getType() {
		return null;
	}
}
