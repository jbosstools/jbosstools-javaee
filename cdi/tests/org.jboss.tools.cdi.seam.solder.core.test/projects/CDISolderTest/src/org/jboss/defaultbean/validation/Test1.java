package org.jboss.defaultbean.validation;

import javax.enterprise.inject.Produces;

import org.jboss.solder.bean.defaultbean.DefaultBean;

public class Test1 {

	@Produces
	@DefaultBean(String.class) 
	@Q
	String s;

}
