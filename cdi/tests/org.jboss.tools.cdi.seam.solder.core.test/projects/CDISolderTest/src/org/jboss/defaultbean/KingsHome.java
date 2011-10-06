package org.jboss.defaultbean;

import javax.enterprise.inject.Produces;

import org.jboss.solder.bean.defaultbean.DefaultBean;

public class KingsHome implements Home {

	@Produces
	@Big
	@DefaultBean(Home.class)
	KingsHome getDefault() {
		return this;
	}

	@Produces
	@Huge
	@DefaultBean(Home.class)
	KingsHome getExclusive() {
		return this;
	}


}
