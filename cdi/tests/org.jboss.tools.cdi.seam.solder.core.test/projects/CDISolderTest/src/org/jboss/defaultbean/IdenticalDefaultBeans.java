package org.jboss.defaultbean;

import javax.enterprise.inject.Produces;

import org.jboss.solder.bean.defaultbean.DefaultBean;

@DefaultBean(IdenticalDefaultBeans.class)
@Cozy
public class IdenticalDefaultBeans {
	
	@Produces
	@DefaultBean(IdenticalDefaultBeans.class)
	@Cozy
	IdenticalDefaultBeans bean;
	
	@Produces
	@DefaultBean(IdenticalDefaultBeans.class)
	@Cozy
	IdenticalDefaultBeans createBean() {
		return null;
	}

}
