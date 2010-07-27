package org.jboss.tools.cdi.text.ext.test;


import org.eclipse.jdt.internal.core.JavaModelManager;
import org.jboss.tools.cdi.core.test.CDICoreTestSetup;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CdiTextExtAllTests {
	public static Test suite() {
		// it could be done here because it is not needed to be enabled back
		JavaModelManager.getIndexManager().disable();
		
		TestSuite suite = new TestSuite(CdiTextExtAllTests.class.getName());
		suite.addTest(new CDICoreTestSetup(new TestSuite(InjectedPointHyperlinkDetectorTest.class)));
		suite.addTest(new CDICoreTestSetup(new TestSuite(ProducerDisposerHyperlinkDetectorTest.class)));
		suite.addTest(new CDICoreTestSetup(new TestSuite(EventAndObserverMethodHyperlinkDetectorTest.class)));
		return suite;
	}
}
