package org.jboss.jsr299.tck.tests.lookup.injection.alternative;

import javax.enterprise.inject.Produces;

public class C {

	@Produces
	public B getB() {
		return new B(200);
	}

}
