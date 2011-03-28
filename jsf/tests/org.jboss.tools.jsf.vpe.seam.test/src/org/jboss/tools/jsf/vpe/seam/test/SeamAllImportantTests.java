package org.jboss.tools.jsf.vpe.seam.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite containing all important Seam tests
 * 
 * @author Yahor Radtsevich (yradtsevich)
 */
public class SeamAllImportantTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(SeamAllImportantTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(JBIDE1484Test.class);
		suite.addTestSuite(OpenOnForDecorateTest.class);
		//$JUnit-END$
		return suite;
	}

}
