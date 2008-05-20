package org.jboss.tools.struts.text.tests;

import org.jboss.tools.struts.text.tests.hyperlink.StrutsJbide1762Test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class StrutsTextExtAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(StrutsTextExtAllTests.class.getName());
		suite.addTest(StrutsJbide1762Test.suite());
		return suite;
	}

}
