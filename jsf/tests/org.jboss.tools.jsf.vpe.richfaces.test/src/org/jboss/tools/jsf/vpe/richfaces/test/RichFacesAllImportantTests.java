package org.jboss.tools.jsf.vpe.richfaces.test;

import org.jboss.tools.jsf.vpe.richfaces.test.jbide.RichFacesJBIDE1169Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite containing all important RichFaces tests
 * 
 * @author Yahor Radtsevich (yradtsevich)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
	RichFacesJBIDE1169Test.class,
	RichFacesInplaceSelectTemplateTestCase.class,
	RichFacesFileUploadTemplateTestCase.class
})
public class RichFacesAllImportantTests {

}
