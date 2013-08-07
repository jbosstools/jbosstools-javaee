package org.jboss.jsr299.tck.tests.lookup.injection.alternative;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

@Alternative
public class A {  
	
	@Produces
	public B getB() { 
		return new B(100);
	}

}
