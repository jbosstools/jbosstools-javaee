package org.jboss.tools.cdi.text.ext.test;


import org.jboss.tools.cdi.core.test.CDICoreTestSetup;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CdiTextExtAllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite(CdiTextExtAllTests.class.getName());
		suite.addTest(new CDICoreTestSetup(CDITextExtTestSuite.suite()));
		return suite;
	}
}
