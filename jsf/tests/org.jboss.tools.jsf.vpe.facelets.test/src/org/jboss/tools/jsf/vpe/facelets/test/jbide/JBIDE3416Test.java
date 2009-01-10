package org.jboss.tools.jsf.vpe.facelets.test.jbide;

import org.jboss.tools.jsf.vpe.facelets.test.FaceletsComponentTest;
import org.jboss.tools.vpe.ui.test.ComponentContentTest;

public class JBIDE3416Test extends ComponentContentTest {

    public JBIDE3416Test(String name) {
	super(name);
    }

    public void testJBIDE3416() throws Throwable {	
	performContentTest("JBIDE/3416/jbide3416.xhtml"); //$NON-NLS-1$
    }
    
    @Override
    protected String getTestProjectName() {
	return FaceletsComponentTest.IMPORT_PROJECT_NAME;
    }

}
