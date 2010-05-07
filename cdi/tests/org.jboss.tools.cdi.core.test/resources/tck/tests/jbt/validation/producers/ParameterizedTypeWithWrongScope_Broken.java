package org.jboss.jsr299.tck.tests.jbt.validation.producers;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class ParameterizedTypeWithWrongScope_Broken {

	@Produces public FunnelWeaver<String> getAnotherFunnelWeaver;

	@AnotherScope
	@Produces public FunnelWeaver<String> getAnotherFunnelWeaver2;

	@Dependent @Produces public FunnelWeaver<String> getAnotherFunnelWeaver3;

	@AnotherScope @Produces public String getAnotherFunnelWeaver4;

	@FishStereotype
	@Produces public FunnelWeaver<String> getAnotherFunnelWeaver5;

	@Produces public FunnelWeaver<String> create(InjectionPoint point) {
		return null;
	}

	@AnotherScope
	@Produces
	public FunnelWeaver<String> create2(InjectionPoint point) {
		return null;
	}

	@Dependent @Produces public FunnelWeaver<String> create3(InjectionPoint point) {
		return null;
	}

	@AnotherScope @Produces public String create4(InjectionPoint point) {
		return null;
	}

	@FishStereotype
	@Produces
	public FunnelWeaver<String> create5(InjectionPoint point) {
		return null;
	}
}