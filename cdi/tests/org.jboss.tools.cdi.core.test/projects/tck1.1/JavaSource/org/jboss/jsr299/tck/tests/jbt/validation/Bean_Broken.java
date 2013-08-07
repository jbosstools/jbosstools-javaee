package org.jboss.jsr299.tck.tests.jbt.validation;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

public class Bean_Broken {

	public void foo() {
		String s = "#{string.ss}";
		String s2 = "#{string.ss}";
	}
	
	@Named
	@Produces
	public String string() {
		return "";  
	}
}