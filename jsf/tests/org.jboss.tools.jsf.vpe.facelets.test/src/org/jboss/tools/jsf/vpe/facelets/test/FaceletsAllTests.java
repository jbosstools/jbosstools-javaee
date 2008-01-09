package org.jboss.tools.jsf.vpe.facelets.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FaceletsAllTests {

    public static Test suite() {

	TestSuite suite = new TestSuite("Tests for Vpe Facelets components");
	// $JUnit-BEGIN$

	suite.addTestSuite(FaceletsComponentTest.class);

	// $JUnit-END$
	return suite;

    }
}
