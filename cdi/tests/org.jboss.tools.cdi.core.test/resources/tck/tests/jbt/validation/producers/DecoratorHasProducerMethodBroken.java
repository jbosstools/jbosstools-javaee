package org.jboss.jsr299.tck.tests.jbt.validation.producers;

import javax.decorator.Decorator;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@Decorator
public class DecoratorHasProducerMethodBroken {

	@Produces
	public FunnelWeaver<String> create2(InjectionPoint point) {
		return null;
	}
}