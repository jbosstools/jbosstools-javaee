package org.jboss.generic;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.seam.solder.bean.generic.ApplyScope;
import org.jboss.seam.solder.bean.generic.Generic;
import org.jboss.seam.solder.bean.generic.GenericConfiguration;

@GenericConfiguration(MyGenericType.class)
public class MyGenericBean2 {
	@Inject
	@Generic
	MyConfiguration config;

	@Inject
	@Generic
	MyBean c;
	
	@Inject
	void setMyBean(@Generic MyBean c) {}

	@Inject
	@Generic
	MyBean3 c3;	

	@Inject
	MyGenericType type;
	
	@Produces
	MyBean2 createMySecondBean() {
		//use config here
		return new MyBean2("");
	}

	@Produces @ApplyScope
	MyBean4 myFourthBean = new MyBean4("bean4");

	void myObserver(@Observes MyBean bean) {
		
	}

	//add other producers
}
