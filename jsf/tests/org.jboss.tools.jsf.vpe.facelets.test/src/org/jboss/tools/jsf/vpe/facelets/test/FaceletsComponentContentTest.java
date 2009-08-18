package org.jboss.tools.jsf.vpe.facelets.test;

import org.jboss.tools.vpe.ui.test.ComponentContentTest;

public class FaceletsComponentContentTest extends ComponentContentTest {

	
	/**
	 * Constructor
	 */
	public FaceletsComponentContentTest(String name) {
		super(name);
		setCheckWarning(false);
	}

	public void testCompositionWithTaglibs() throws Throwable {
		performContentTest("components/compositionWithTaglibs.xhtml");//$NON-NLS-1$
	}
	
	public void testCompositionWithoutTaglibs() throws Throwable {
		performContentTest("components/compositionWithoutTaglibs.xhtml");//$NON-NLS-1$
	}
	
	public void testCompositionErrorMessage() throws Throwable {
		performContentTest("components/composition_errorMessage.xhtml");//$NON-NLS-1$
	}

	public void testCustomFaceletComponent() throws Throwable {
		performContentTest("components/customFaceletComponent.xhtml");//$NON-NLS-1$
	}

	@Override
	protected String getTestProjectName() {
		return FaceletsAllTests.IMPORT_PROJECT_NAME;
	}
}
