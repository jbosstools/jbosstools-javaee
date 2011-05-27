package org.jboss.generic;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.seam.solder.bean.generic.ApplyScope;
import org.jboss.seam.solder.bean.generic.Generic;
import org.jboss.seam.solder.bean.generic.GenericConfiguration;

@GenericConfiguration(MyGenericType.class)
public class MyGenericBean {
	@Inject
	@Generic
	MyConfiguration config;

	@Inject
	MyGenericType type;
	
	@Produces @ApplyScope
	MyBean createMyFirstBean() {
		//use config here
		return new MyBean("bean1");
	}

	@Produces @ApplyScope
	MyBean3 myThirdBean = new MyBean3("bean3");

	//add other producers
}
