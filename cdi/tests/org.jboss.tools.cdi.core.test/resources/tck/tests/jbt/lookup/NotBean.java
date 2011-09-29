package org.jboss.jsr299.tck.tests.jbt.lookup;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

@Named("abstractClass")
public abstract class NotBean {

	@Produces
	@Named("producerInAbstractClass") NotBean b;

	@Inject @Named("abstractClass") NotBean f1;
	@Inject @Named("producerInAbstractClass") NotBean f2;
}
