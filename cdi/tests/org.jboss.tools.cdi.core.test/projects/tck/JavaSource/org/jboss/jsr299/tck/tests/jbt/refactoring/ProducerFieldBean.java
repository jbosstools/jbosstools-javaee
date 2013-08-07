package org.jboss.jsr299.tck.tests.jbt.refactoring;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

public class ProducerFieldBean {
	   @Produces @Named("sField") String s;
	   
	   String s1 = "#{sField.charAt(0)}";
}
