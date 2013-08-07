package org.jboss.jsr299.tck.tests.jbt.resolution.defaultconstructors;

import java.io.File;

import javax.inject.Inject;

public class ProjectImpl2 {

	@Inject
	public ProjectImpl2(final File rootDirectory) {
	}
}