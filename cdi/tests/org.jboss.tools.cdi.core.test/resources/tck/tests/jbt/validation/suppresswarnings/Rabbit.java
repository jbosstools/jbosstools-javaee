package org.jboss.jsr299.tck.tests.jbt.validation.suppresswarnings;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;

@SuppressWarnings("notPassivationCapableBean") 
@SessionScoped
public class Rabbit {

	@SuppressWarnings("illegalTypeInTypedDeclaration")
	@Produces
	@Typed(Integer.class)
	String s;
}
