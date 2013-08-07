package org.jboss.jsr299.tck.tests.jbt.validation;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class TestNamed {

	@SuppressWarnings("cdi-ambiguous-dependency")
	@Inject String s; // Ambiguous 

	@Produces 
	public String foo1;

	@Produces
	public String foo2;
}