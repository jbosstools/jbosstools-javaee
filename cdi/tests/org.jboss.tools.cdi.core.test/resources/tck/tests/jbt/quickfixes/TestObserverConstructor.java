package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class TestObserverConstructor {
	
	@Inject
	public TestObserverConstructor(@Observes String aaa){
		
	}
}
