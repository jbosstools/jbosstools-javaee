package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class TestInjectDisposerMethod {
	@Produces
	public String produce(){
		return "test";
	}
	
	@Inject
	public void dispose(@Disposes String aaa){
	}
}
