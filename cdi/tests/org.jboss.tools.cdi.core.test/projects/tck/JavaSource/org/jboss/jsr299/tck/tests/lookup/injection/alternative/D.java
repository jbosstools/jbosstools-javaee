package org.jboss.jsr299.tck.tests.lookup.injection.alternative;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Injection point is resolved to producer bean A.getB().
 * Bean C.getB() is eliminated, because A.getB() is declared in selected alternative class bean A. * 
 * 
 * @author slava
 *
 */
public class D {

	@Inject 
	B b;

}
