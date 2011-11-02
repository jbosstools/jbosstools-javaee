package org.jboss.defaultbean;
import javax.enterprise.inject.Produces;

import org.jboss.defaultbean.validation2.*;
import org.jboss.solder.bean.defaultbean.DefaultBean;

public class Test22 {
	@Produces
	@DefaultBean(String.class) 
	@Q
	@R
	String getString() {
		return "";
	}

}
