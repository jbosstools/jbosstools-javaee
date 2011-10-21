package org.jboss.defaultbean.validation;

import javax.enterprise.inject.Produces;

import org.jboss.solder.bean.defaultbean.DefaultBean;

@DefaultBean(Test3.class) 
@Q
public class Test3 {

	@Produces
	@Q
	Test3 s2;

	@Produces
	@Q
	Test3 getTest() {
		return null;
	}
}
