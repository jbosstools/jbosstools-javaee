/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.VpeEditorPart;

/**
 * @author mareshkau
 *	junit http://jira.jboss.org/jira/browse/JBIDE-2434
 */
public class JBIDE2434Test extends VpeTest{

	public JBIDE2434Test(String name) {
		super(name);
	}
	/**
	 * tests open and close editor in page
	 * @throws Throwable
	 */
	public void testOpenAndCloPageWithCycleFacelets() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("JBIDE/2434/FaceletForm.xhtml",JsfAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	/**
	 * Tests visual refresh method
	 * @throws Throwable
	 */
	public void testVisualRefreshAndSwitchToPreview() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		// Tests CA
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/2434/FaceletBlank.xhtml", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file " + "JBIDE/2434/FaceletBlank.xhtml", file); //$NON-NLS-1$ //$NON-NLS-2$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		VpeController controller = TestUtil.getVpeController(part);
		controller.visualRefresh();
		TestUtil.waitForJobs();
		part.getVisualEditor().setVisualMode(0);
		part.getVisualEditor().setVisualMode(1);
		((VpeEditorPart)part.getVisualEditor()).createPreviewBrowser();
		part.getVisualEditor().setVisualMode(2);

		TestUtil.waitForJobs();
		if(getException()!=null) {
			throw getException();
		}
	}
		
}
