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

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.ui.test.OpenOnUtil;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Sergey Dzmitrovich
 * 
 */
public class OpenOnInsideJspRoot_JBIDE4852 extends VpeTest {

	private static final String TEST_PAGE_NAME = "JBIDE/4852/openOnTest.jsp"; //$NON-NLS-1$

	private static String INCLUDE_TAG_ID = "openOn"; //$NON-NLS-1$

	private static String PAGE_ATTRIBUTE = "page"; //$NON-NLS-1$

	private static String PAGE_ATTRIBUTE_VALUE = "include.jsp"; //$NON-NLS-1$

	public OpenOnInsideJspRoot_JBIDE4852(String name) {
		super(name);
	}

	public void testOpenOnLinkStyles() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME,
				JsfAllTests.IMPORT_PROJECT_NAME);

		assertNotNull("Could not open specified file. componentPage = "
				+ TEST_PAGE_NAME
				+ ";projectName = " + JsfAllTests.IMPORT_PROJECT_NAME, file);//$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input);
		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get controller
		VpeController controller = TestUtil.getVpeController(part);
		assertNotNull(controller);

		// get source document
		Document sourceDocument = getSourceDocument(controller);
		assertNotNull(sourceDocument);

		Element openOnTestedElement = sourceDocument
				.getElementById(INCLUDE_TAG_ID);
		Attr testedClassAttr = openOnTestedElement
				.getAttributeNode(PAGE_ATTRIBUTE);

		OpenOnUtil.performOpenOnAction(part.getSourceEditor(),
				((IDOMAttr) testedClassAttr).getValueRegionStartOffset() + 1);

		IEditorPart editorPart = (IEditorPart) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();

		TestUtil.waitForJobs();

		assertNotNull(editorPart);

		assertTrue(editorPart.getEditorInput().getName().endsWith(
				PAGE_ATTRIBUTE_VALUE));

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

}
