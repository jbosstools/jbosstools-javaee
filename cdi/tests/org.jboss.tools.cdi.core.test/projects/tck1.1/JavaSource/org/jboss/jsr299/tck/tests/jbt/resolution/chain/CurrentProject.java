package org.jboss.jsr299.tck.tests.jbt.resolution.chain;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CurrentProject {
	@Produces
	@Default
	@Dependent
	public Project getCurrent() {
		return null;
	}

	@Inject Project project;
}
