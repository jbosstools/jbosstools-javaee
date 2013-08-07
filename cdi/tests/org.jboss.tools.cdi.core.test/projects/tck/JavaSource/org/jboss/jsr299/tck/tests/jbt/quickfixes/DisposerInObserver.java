package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

public class DisposerInObserver {
	@Produces
	public String produce(){
		return "a";
	}
	
	public void method(@Observes Boolean param1, @Disposes Boolean param2){
		
	}
}
