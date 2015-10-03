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
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite containing all important RichFaces tests
 * 
 * @author Yahor Radtsevich (yradtsevich)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
	Jbide1580Test.class,
	JBIDE1613Test.class,
	Jbide1614Test.class,
	Jbide1639Test.class,
	Jbide1682Test.class,
	Jbide1548Test.class,
	JBIDE1713Test.class,
	RichFacesJBIDE1169Test.class,
	RichFacesComboBoxTemplateTestCase.class,
	RichFacesInplaceSelectTemplateTestCase.class,
	RichFacesFileUploadTemplateTestCase.class,
	RichFacesColumnsTemplateTestCase.class,
	RichFacesPickListTemplateTestCase.class,
	JBIDE1606Test.class
})
public class RichFacesAllImportantTests {

}
