package org.jboss.jsr299.tck.tests.jbt.core;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class NamedElement {

	@Produces int i;

	@Produces
	public String getFoo() {
		return "";
	}

	@Inject
	private void injectFoo(String arg1) {
	}
}