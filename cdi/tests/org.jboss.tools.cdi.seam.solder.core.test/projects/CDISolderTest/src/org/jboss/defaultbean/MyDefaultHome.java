package org.jboss.defaultbean;

import javax.enterprise.inject.Produces;

import org.jboss.solder.bean.defaultbean.DefaultBean;

@DefaultBean(Home.class)
@Small
public class MyDefaultHome implements Home {

	@Produces
	@Cozy
	Home cozy = new MyDefaultHome();

	@Produces
	Home old = new MyDefaultHome();

}
