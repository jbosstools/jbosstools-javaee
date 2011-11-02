package org.jboss.defaultbean;

import javax.enterprise.inject.Produces;
import org.jboss.solder.bean.defaultbean.DefaultBean;
import org.jboss.defaultbean.validation2.*;

public class Test21 {
	@Produces
	@DefaultBean(String.class) 
	@Q
	@R
	String getString() {
		return "";
	}

}
