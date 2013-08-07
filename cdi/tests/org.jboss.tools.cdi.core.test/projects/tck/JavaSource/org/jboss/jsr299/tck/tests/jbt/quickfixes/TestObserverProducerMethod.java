package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

public class TestObserverProducerMethod {
	@Produces
	public String produceString(@Observes String aa){
		return "test";
	}
}
