package org.jboss.tools.seam.core.test.project.facet;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Seam20XFacetTestSuite211GABase extends Seam20XFacetTestSuite201GA {

	public static Test suite() {
		TestSuite suite = new TestSuite("Seam 2.1.* tests");
		suite.addTest(new Seam2FacetInstallDelegateTestSetup(new TestSuite(Seam211GAFacetInstallDelegateTest.class)));
		return suite;
	}

	public Seam20XFacetTestSuite211GABase() {
		super();
	}

}