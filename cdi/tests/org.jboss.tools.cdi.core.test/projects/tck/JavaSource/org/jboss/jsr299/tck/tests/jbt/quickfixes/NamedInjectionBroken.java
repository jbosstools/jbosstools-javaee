package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.inject.Inject;
import javax.inject.Named;

public class NamedInjectionBroken {

	@Named @Inject Order order;

	@Named("e")
	@Inject
	NamedInjectionBroken(Order order) {
		// DO nothing
	}

	@Inject public void init(@Named Order order) {
		// DO nothing
	}

	@Named("injectTestFooName") @Inject
	public void foo(Order order) {
		// DO nothing
	}
}