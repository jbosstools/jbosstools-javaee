package org.jboss.jsr299.tck.tests.jbt.validation.inject.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

@ApplicationScoped
public class ProducerWInjections {

	@Inject
	public ProducerWInjections(InjectionPoint broken) {
	}

	@Produces
    public Test produceOk(InjectionPoint ok) {
    	return null;
    }

	@Produces
	@ApplicationScoped
    public Test produceBroken(InjectionPoint broken) {
    	return null;
    }

    public void disposeBroken(@Disposes Test arg1, InjectionPoint broken) {
    }

    public void observeBroken(@Observes Test arg1, InjectionPoint broken) {
    }

    public static class Test {
    }
}