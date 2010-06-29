/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.custom.StyledText;
import org.jboss.tools.common.el.core.ELReferenceList;
import org.jboss.tools.common.resref.core.ResourceReference;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.ui.test.ComponentContentTest;
import org.jboss.tools.vpe.ui.test.TestUtil;

/**
 * @author mareshkau
 *
 */
public class JBIDE4179Test extends ComponentContentTest{
	
	private static final String testName = "JBIDE/4179/jbide4179.xhtml"; //$NON-NLS-1$
	
	private IFile file;
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.ui.test.VpeTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.file = (IFile) TestUtil.getComponentPath(JBIDE4179Test.testName,JsfAllTests.IMPORT_PROJECT_NAME);
	    ResourceReference[] entries = new ResourceReference[1];
	    entries[0] = new ResourceReference("false", ResourceReference.FILE_SCOPE); //$NON-NLS-1$
	    entries[0].setProperties("false"); //$NON-NLS-1$
		ELReferenceList.getInstance().setAllResources(this.file,entries);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.ui.test.VpeTest#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		 ELReferenceList.getInstance().setAllResources(this.file, new ResourceReference[0]);
		super.tearDown();
	}
	
	public JBIDE4179Test(String name) {
		super(name);
	}
	
	public void testJBIDE4179Test() throws Throwable {
		VpeController vpeController =openInVpe(JsfAllTests.IMPORT_PROJECT_NAME, "JBIDE/4179/jbide4179.xhtml"); //$NON-NLS-1$		
		StyledText styledText = vpeController.getSourceEditor().getTextViewer()
				.getTextWidget();
		styledText.setCaretOffset(TestUtil.getLinePositionOffcet( vpeController.getSourceEditor().getTextViewer(),
				13, 51));
		styledText.insert("b");  //$NON-NLS-1$
		//wait when update job finished
		TestUtil.delay(500);
		File xmlTestFile = TestUtil.getComponentPath(
				JBIDE4179Test.testName + XML_FILE_EXTENSION, getTestProjectName())
				.getLocation().toFile();

		// get document
		compareContent(vpeController, xmlTestFile);

		if (getException() != null) {
			throw getException();
		}
	}

	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_PROJECT_NAME;
	}
}
