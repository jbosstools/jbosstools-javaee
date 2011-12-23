package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.base.test.ComponentContentTest;

public class JsfLinkAndCommandLinkTest_JBIDE8009 extends ComponentContentTest {

	public JsfLinkAndCommandLinkTest_JBIDE8009(String name) {
		super(name);
	}

	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_PROJECT_NAME;
	}

	public void testCommandLink() throws Throwable {
		performContentTest("JBIDE/8009/JsfLinkAndCommandLink.jsp"); //$NON-NLS-1$
	}
	
}
