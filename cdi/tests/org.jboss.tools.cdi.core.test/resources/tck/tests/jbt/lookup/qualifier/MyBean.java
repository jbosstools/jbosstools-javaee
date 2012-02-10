package org.jboss.jsr299.tck.tests.jbt.lookup.qualifier;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class MyBean {
	
	@Produces
	@QualifierWithDefaults
	String p;

	@Inject
	@QualifierWithDefaults
	String i;

}
