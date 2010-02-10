package org.jboss.tools.cdi.text.ext.test;


import junit.framework.Test;
import junit.framework.TestSuite;

public class CdiTextExtAllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite(CdiTextExtAllTests.class.getName());
		suite.addTest(InjectedPointHyperlinkDetectorTest.suite());
		return suite;
	}
}
