package org.jboss.jsr299.tck.tests.jbt.validation;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class NPEValidation extends UnResolvedClass implements UnResolvedInterface {

	@Inject UnResolvedType t;

	@Inject
	public UnResolvedType set(UnResolvedType p) {
	}

	@Produces
	public UnResolvedType produce() {
	}
}