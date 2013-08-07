package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class TestInjectObserverMethod {
	
	@Inject
	public void observ(@Observes String aaa){
	}
}
