package org.jboss.jsr299.tck.tests.jbt.resolution.defaultconstructors;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CurrentProject {

	@Inject
	Project project;

	@Inject
	ProjectImpl2 project2;

	@Produces
	@Default
	@Dependent
	public Project getCurrent() {
		return null;
	}

	@Produces
	@Default
	@Dependent
	public ProjectImpl2 getCurrent2() {
		return null;
	}
}