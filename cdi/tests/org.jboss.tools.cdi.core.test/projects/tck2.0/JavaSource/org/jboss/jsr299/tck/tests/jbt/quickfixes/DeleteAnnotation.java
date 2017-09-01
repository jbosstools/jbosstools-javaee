package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

@Named("sss")
public class DeleteAnnotation {
	@Produces
	public String produce(){
		return "test";
	}
	
	
	@Inject
	public DeleteAnnotation(String aaa){
		
	}
}
