package org.jboss.jsr299.tck.tests.jbt.validation.suppresswarnings;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;

@SessionScoped
public class AnotherRabbit {

	@Produces
	@Typed(Integer.class)
	String s;
}
