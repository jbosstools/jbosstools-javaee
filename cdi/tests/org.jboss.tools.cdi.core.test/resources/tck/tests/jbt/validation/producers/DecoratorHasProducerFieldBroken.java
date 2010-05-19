package org.jboss.jsr299.tck.tests.jbt.validation.producers;

import javax.decorator.Decorator;
import javax.enterprise.inject.Produces;

@Decorator
public class DecoratorHasProducerFieldBroken {

	@Produces public FunnelWeaver<String> getAnotherFunnelWeaver;
}