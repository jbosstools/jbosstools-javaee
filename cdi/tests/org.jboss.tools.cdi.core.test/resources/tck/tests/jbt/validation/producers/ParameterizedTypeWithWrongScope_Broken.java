package org.jboss.jsr299.tck.tests.jbt.validation.producers;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;

public class ParameterizedTypeWithWrongScope_Broken <T> {

	@Produces public FunnelWeaver<T> getAnotherFunnelWeaver;

	@AnotherScope
	@Produces public FunnelWeaver<T> getAnotherFunnelWeaver2;

	@Dependent @Produces public FunnelWeaver<T> getAnotherFunnelWeaver3;

	@AnotherScope @Produces public String getAnotherFunnelWeaver4;

	@FishStereotype
	@Produces public FunnelWeaver<T> getAnotherFunnelWeaver5;

	@Produces public FunnelWeaver<T> create(InjectionPoint point) {
		return null;
	}

	@AnotherScope
	@Produces
	public <E> FunnelWeaver<E> create2(InjectionPoint point) {
		return null;
	}

	@Dependent @Produces public <E> FunnelWeaver<E> create3(InjectionPoint point) {
		return null;
	}

	@AnotherScope @Produces public String create4(InjectionPoint point) {
		return null;
	}

	@FishStereotype
	@Produces
	public FunnelWeaver<T> create5(InjectionPoint point) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Produces
	@Named
	@RequestScoped
	public List<String> getUsers() {
		return null;
	}

	@AnotherScope
	@Produces public FunnelWeaver<String> getAnotherFunnelWeaver7;
}