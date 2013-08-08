package org.jboss.jsr299.tck.tests.jbt.validation.inject;

import javax.inject.Inject;

public class GenericInitializerMethodBroken {

	@Inject
	public <U> void genericFoo(U arg) {
	}

	@Inject
	public static void staticFoo(String arg) {
	}
}