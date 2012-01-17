package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.event.Observes;


public class MultipleObservers {
	public void method(@Observes Boolean param, @Observes Boolean param2, @Observes Boolean param3){
		
	}
}
