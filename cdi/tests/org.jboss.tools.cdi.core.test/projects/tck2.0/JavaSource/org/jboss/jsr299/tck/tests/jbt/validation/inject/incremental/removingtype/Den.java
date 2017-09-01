package org.jboss.jsr299.tck.tests.jbt.validation.inject.incremental.removingtype;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Den {

	@Inject
	@Hibernation
	private Bear bear;
}
