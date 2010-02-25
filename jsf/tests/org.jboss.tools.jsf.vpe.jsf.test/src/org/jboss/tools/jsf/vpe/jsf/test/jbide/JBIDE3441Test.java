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

import java.io.IOException;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * Test for JIRA Issue JBIDE-3441 ( https://jira.jboss.org/jira/browse/JBIDE-3441 ):
 * "VPE - Preferences - change size of VE pane - press OK - Preview will show empty page"
 * 
 * @author yradtsevich
 *
 */
public class JBIDE3441Test  extends VpeTest {
	private static final String TAGGED_DIV_ID = "tagged-div"; //$NON-NLS-1$
	private static final String TAGGED_DIV_CONTENT = "tagged div content"; //$NON-NLS-1$
	private static final String TEST_PAGE_NAME_1 = "JBIDE/3441/JBIDE-3441-1.html"; //$NON-NLS-1$
	private static final String TEST_PAGE_NAME_2 = "JBIDE/3441/JBIDE-3441-2.html"; //$NON-NLS-1$

	public JBIDE3441Test(String name) {
		super(name);
	}
	
	/**
	 * Try to open two pages in VPE and refresh them n times.
	 */
	public void testVisualRefresh() throws Throwable {
		setException(null);

		VpeController controller1 = openPageInVpe(TEST_PAGE_NAME_1);
		VpeController controller2 = openPageInVpe(TEST_PAGE_NAME_2);

		for (int i = 0; i < 5; i++) {
			controller1.visualRefresh();
			controller2.visualRefresh();
			TestUtil.delay(5000);
			TestUtil.waitForJobs();
			checkTaggedDivValue(controller1);
			checkTaggedDivValue(controller2);	
		}

		if (getException() != null) {
			throw getException();
		}
	}

	private void checkTaggedDivValue(VpeController controller) {
		nsIDOMElement taggedDiv = controller.getXulRunnerEditor().getDOMDocument().getElementById(TAGGED_DIV_ID);
		assertNotNull("taggedDiv should be not null", taggedDiv); //$NON-NLS-1$
		nsIDOMNode innerSpan = taggedDiv.getFirstChild();
		assertNotNull("taggedDiv should have inner span", innerSpan); //$NON-NLS-1$
		nsIDOMNode taggedDivTextNode = innerSpan.getFirstChild();
		assertNotNull("taggedDiv should have inner span with text node inside", taggedDivTextNode); //$NON-NLS-1$
		String taggedDivTextValue = taggedDivTextNode.getNodeValue();
		Assert.assertEquals("body of taggedDiv should be equal to \'" + TAGGED_DIV_CONTENT //$NON-NLS-1$ 
				+ "\', but it is \'" + taggedDivTextValue + "\'", //$NON-NLS-1$  //$NON-NLS-2$
				taggedDivTextValue, TAGGED_DIV_CONTENT);
	}

	private VpeController openPageInVpe(final String pageName) throws CoreException,
			PartInitException, IOException {
		IFile elementPageFile = (IFile) TestUtil.getComponentPath(
				pageName, JsfAllTests.IMPORT_PROJECT_NAME);
		IEditorInput input = new FileEditorInput(elementPageFile);

		JSPMultiPageEditor editor = (JSPMultiPageEditor) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().openEditor(input,
						EDITOR_ID, true);
		assertNotNull(editor);

		VpeController controller = TestUtil.getVpeController(editor);
		return controller;
	}
}
