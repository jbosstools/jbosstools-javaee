package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

public class TestDisposerProducerMethod {
	@Produces
	public String produceString(@Disposes String aaa){
		return "test";
	}
}
