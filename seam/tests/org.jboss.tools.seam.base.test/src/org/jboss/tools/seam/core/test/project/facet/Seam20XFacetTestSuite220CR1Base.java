package org.jboss.tools.seam.core.test.project.facet;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Seam20XFacetTestSuite220CR1Base extends Seam20XFacetTestSuite201GA {

	public static Test suite() {
		TestSuite suite = new TestSuite("Seam 2.2.* tests");
		suite.addTest(new Seam2FacetInstallDelegateTestSetup(new TestSuite(Seam220CR1FacetInstallDelegateTest.class)));
		return suite;
	}

	public Seam20XFacetTestSuite220CR1Base() {
		super();
	}

}