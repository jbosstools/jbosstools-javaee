package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class TestInjectProducerMethod {
	@Inject
	@Produces
	public String produceString(){
		return "test";
	}
}
