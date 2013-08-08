package org.jboss.jsr299.tck.tests.jbt.refactoring;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

public class ProducerMethodBean {
	   @Produces
	   @Named("infoMethod")
	   String info(){
		   return "info";
	   }
	   
	   String s1 = "#{infoMethod.charAt(0)}";
}
