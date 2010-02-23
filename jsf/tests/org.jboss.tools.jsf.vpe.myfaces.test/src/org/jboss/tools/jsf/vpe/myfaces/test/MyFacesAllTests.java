package org.jboss.tools.jsf.vpe.myfaces.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.vpe.ui.test.VpeTestSetup;

/**
 * The Class MyFacesAllTests.
 */
public class MyFacesAllTests {

	/**
	 * Suite.
	 * 
	 * @return the test
	 */
	public static Test suite() {

		TestSuite suite = new TestSuite("Tests for Vpe MyFaces components"); //$NON-NLS-1$

		// $JUnit-BEGIN$
		suite.addTestSuite(MyFacesComponentTest.class);
		// $JUnit-END$
		return new VpeTestSetup(suite);
	}
}
