package org.jboss.jsr299.tck.tests.jbt.inject;

import javax.inject.Inject;
import javax.inject.Named;

public class NamedInjectionBroken {

	@Named @Inject Order order;

	@Named
	@Inject
	NamedInjectionBroken(Order order) {
		// DO nothing
	}

	@Named @Inject public void init(Order order) {
		// DO nothing
	}

	@Named("injectTestFooName") @Inject
	public void foo(Order order) {
		// DO nothing
	}
}