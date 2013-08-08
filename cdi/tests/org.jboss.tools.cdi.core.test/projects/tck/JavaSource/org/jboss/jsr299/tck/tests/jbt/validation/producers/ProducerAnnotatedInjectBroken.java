package org.jboss.jsr299.tck.tests.jbt.validation.producers;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class ProducerAnnotatedInjectBroken {

	@Produces @Inject public FunnelWeaver<String> anotherFunnelWeaver;
}