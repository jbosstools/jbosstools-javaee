package org.jboss.jsr299.tck.tests.jbt.lookup;

import javax.inject.Inject;
import javax.enterprise.inject.Any;

public class ObjectInjection {
	@Inject @Any Object object;
}
