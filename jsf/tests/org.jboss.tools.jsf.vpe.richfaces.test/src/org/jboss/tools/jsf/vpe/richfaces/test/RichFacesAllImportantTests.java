package org.jboss.tools.jsf.vpe.richfaces.test;

import org.jboss.tools.jsf.vpe.richfaces.test.jbide.JBIDE1606Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.JBIDE1613Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.JBIDE1713Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.Jbide1548Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.Jbide1580Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.Jbide1614Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.Jbide1639Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.Jbide1682Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.RichFacesJBIDE1169Test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite containing all important RichFaces tests
 * 
 * @author Yahor Radtsevich (yradtsevich)
 */
public class RichFacesAllImportantTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				RichFacesAllImportantTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(Jbide1580Test.class);
		suite.addTestSuite(JBIDE1613Test.class);
		suite.addTestSuite(Jbide1614Test.class);
		suite.addTestSuite(Jbide1639Test.class);
		suite.addTestSuite(Jbide1682Test.class);
		suite.addTestSuite(Jbide1548Test.class);
		suite.addTestSuite(JBIDE1713Test.class);
		suite.addTestSuite(RichFacesJBIDE1169Test.class);
		suite.addTestSuite(RichFacesComboBoxTemplateTestCase.class);
		suite.addTestSuite(RichFacesInplaceSelectTemplateTestCase.class);
		suite.addTestSuite(RichFacesFileUploadTemplateTestCase.class);
		suite.addTestSuite(RichFacesColumnsTemplateTestCase.class);
		suite.addTestSuite(RichFacesPickListTemplateTestCase.class);
		suite.addTestSuite(JBIDE1606Test.class);
		//$JUnit-END$
		return suite;
	}

}
