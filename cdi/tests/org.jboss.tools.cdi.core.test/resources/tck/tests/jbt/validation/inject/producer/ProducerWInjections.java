package org.jboss.jsr299.tck.tests.jbt.validation.inject.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class ProducerWInjections {

    @Produces
    public Test produce(InjectionPoint ip) {
    	return null;
    }

    public static class Test {
    }
}