package org.jboss.jsr299.tck.tests.jbt.validation.suppresswarnings;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;

@SuppressWarnings("cdi-not-passivation-capable") 
@SessionScoped
public class Rabbit {

	@SuppressWarnings("cdi-typed")
	@Produces
	@Typed(Integer.class)
	String s;
}
