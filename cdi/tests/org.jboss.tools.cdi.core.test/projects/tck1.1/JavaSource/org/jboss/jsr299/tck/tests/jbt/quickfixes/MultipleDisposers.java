package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;


public class MultipleDisposers {
	@Produces
	public String produce(){
		return "";
	}
	
	public void dispose(@Disposes String param, @Disposes String param2, @Disposes String param3){
		
	}
}
