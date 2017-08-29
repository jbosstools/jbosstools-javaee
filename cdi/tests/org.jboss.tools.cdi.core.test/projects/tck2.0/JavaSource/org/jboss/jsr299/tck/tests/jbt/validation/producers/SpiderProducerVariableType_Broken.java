package org.jboss.jsr299.tck.tests.jbt.validation.producers;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class SpiderProducerVariableType_Broken<T> {

	@Produces public FunnelWeaver<T> getAnotherFunnelWeaver;

	@Produces T getAnotherFunnelWeaver2;

	@Produces
	public T create(InjectionPoint point) {
		return null;
	}
}