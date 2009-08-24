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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.OpenOnUtil;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleRule;

/**
 * @author Sergey Dzmitrovich
 * 
 */
public class OpenOnCssClassTest_JBIDE4775 extends VpeTest {

	private static final String TEST_PAGE_NAME = "JBIDE/4775/openOnTestPage.html"; //$NON-NLS-1$

	private static String ON_PAGE_STYLE_TEST_TAG_ID = "openOn1"; //$NON-NLS-1$

	private static String LINK_STYLE_TEST_TAG_ID = "openOn2"; //$NON-NLS-1$

	private static String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

	public OpenOnCssClassTest_JBIDE4775(String name) {
		super(name);
	}

	public void testOpenOnStylesOnPage() throws Throwable {

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
				.getElementById(ON_PAGE_STYLE_TEST_TAG_ID);
		Attr testedClassAttr = openOnTestedElement
				.getAttributeNode(CLASS_ATTRIBUTE);

		OpenOnUtil.performOpenOnAction(part.getSourceEditor(),
				((IDOMAttr) testedClassAttr).getValueRegionStartOffset() + 1);

		IStructuredSelection selection = (IStructuredSelection) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getSelection();

		TestUtil.waitForJobs();

		Node cssClassNode = (Node) selection.getFirstElement();

		assertEquals(cssClassNode.getParentNode().getLocalName().toLowerCase(),
				HTML.TAG_STYLE.toLowerCase());

		assertTrue(cssClassNode.getNodeValue().contains(
				testedClassAttr.getValue()));

		// check exception
		if (getException() != null) {
			throw getException();
		}

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
				.getElementById(LINK_STYLE_TEST_TAG_ID);
		Attr testedClassAttr = openOnTestedElement
				.getAttributeNode(CLASS_ATTRIBUTE);

		OpenOnUtil.performOpenOnAction(part.getSourceEditor(),
				((IDOMAttr) testedClassAttr).getValueRegionStartOffset() + 1);

		IStructuredSelection selection = (IStructuredSelection) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getSelection();

		TestUtil.waitForJobs();

		CSSStyleRule cssClassNode = (CSSStyleRule) selection.getFirstElement();

		assertTrue(cssClassNode.getSelectorText().contains(
				testedClassAttr.getNodeValue()));

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}
}
