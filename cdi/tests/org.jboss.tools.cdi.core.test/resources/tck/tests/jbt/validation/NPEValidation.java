package org.jboss.jsr299.tck.tests.jbt.validation;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@Decorator
public class NPEValidation extends UnResolvedClass {

	@Inject @Delegate String t;

	@Inject UnResolvedType t1;

	@Inject
	public UnResolvedType set(UnResolvedType p) {
	}

	@Produces
	public UnResolvedType produce() {
	}
}