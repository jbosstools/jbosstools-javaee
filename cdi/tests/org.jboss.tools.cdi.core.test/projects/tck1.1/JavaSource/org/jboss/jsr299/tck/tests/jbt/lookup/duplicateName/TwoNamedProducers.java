package org.jboss.jsr299.tck.tests.jbt.lookup.duplicateName;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

public class TwoNamedProducers {

	@Inject @Named SomeType create;

	@Produces
	@Named
	public SomeType create() {
		return new SomeType();
	}

	@Produces
	@Named("create")
	public SomeType create2() {
		return new SomeType();
	}

	public static class SomeType {
	}
}
