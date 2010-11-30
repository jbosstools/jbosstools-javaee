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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.DocTypeUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentType;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 * 
 * @author sdzmitrovich
 * 
 *         test for http://jira.jboss.com/jira/browse/JBIDE-1718
 * 
 */
public class JsfJbide2170Test extends VpeTest {

	private static final String WITHOUT_DOCTYPE_TEST_PAGE_NAME = "JBIDE/2170/there_is_not_doctype.xhtml"; //$NON-NLS-1$

	private static final String WITH_DOCTYPE_TEST_PAGE_NAME = "JBIDE/2170/there_is_doctype.xhtml"; //$NON-NLS-1$

	private static final String TEMPLATE_DOCTYPE_TEST_PAGE_NAME = "JBIDE/2170/template_doctype_test.xhtml"; //$NON-NLS-1$

	private static final String COMPLEX_DOCTYPE_TEST_PAGE_NAME = "JBIDE/2170/complex_doctype_test.xhtml"; //$NON-NLS-1$

	private static final String EDIT_DOCTYPE_TEST_PAGE_NAME = "JBIDE/2170/edit_doctype_test.xhtml"; //$NON-NLS-1$

	private static final String ELEMENT_ID = "idForCheck"; //$NON-NLS-1$

	private static final String CORRECT_NAME = "html3"; //$NON-NLS-1$

	public JsfJbide2170Test(String name) {
		super(name);
	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void testWithoutDoctypePage() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil
				.getComponentPath(WITHOUT_DOCTYPE_TEST_PAGE_NAME,
						JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get doctype from opened page
		String basicDoctypeString = DocTypeUtil.getDoctype(input);

		// doctype must be not null
		assertNotNull(basicDoctypeString);

		// length of doctype must be 0
		assertEquals(0, basicDoctypeString.length());

		// get dom document from vpe
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);

		// get doctype from vpe's dom model
		nsIDOMDocumentType vpeDocumentType = document.getDoctype();

		// doctype must be null
		assertNull(vpeDocumentType);

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void testWithDoctypePage() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(
				WITH_DOCTYPE_TEST_PAGE_NAME, JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get doctype from opened page
		String basicDoctypeString = DocTypeUtil.getDoctype(input);

		// doctype must be not null
		assertNotNull(basicDoctypeString);
		// length of doctype must be more than 0
		assertEquals(true, basicDoctypeString.length() > 0);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);

		// get doctype
		nsIDOMDocumentType vpeDocumentType = document.getDoctype();

		// doctype must be not null
		assertNotNull(vpeDocumentType);

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void testTemplateDoctypePage() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(
				TEMPLATE_DOCTYPE_TEST_PAGE_NAME,
				JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get doctype from opened page
		String basicDoctypeString = DocTypeUtil.getDoctype(input);

		// doctype must be not null
		assertNotNull(basicDoctypeString);
		// length of doctype must be more than 0
		assertEquals(true, basicDoctypeString.length() > 0);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);

		// get doctype
		nsIDOMDocumentType vpeDocumentType = document.getDoctype();

		// doctype must be not null
		assertNotNull(vpeDocumentType);

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void testComplexDoctypePage() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil
				.getComponentPath(COMPLEX_DOCTYPE_TEST_PAGE_NAME,
						JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get doctype from opened page
		String basicDoctypeString = DocTypeUtil.getDoctype(input);

		// doctype must be not null
		assertNotNull(basicDoctypeString);
		// length of doctype must be more than 0
		assertEquals(true, basicDoctypeString.length() > 0);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);

		// get doctype
		nsIDOMDocumentType vpeDocumentType = document.getDoctype();

		// doctype must be not null
		assertNotNull(vpeDocumentType);

		assertEquals(CORRECT_NAME, vpeDocumentType.getName());
		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void testEditDoctypePage() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(
				EDIT_DOCTYPE_TEST_PAGE_NAME, JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		VpeController controller = TestUtil.getVpeController(part);

		// controller must be not null
		assertNotNull(controller);

		// get document from opened page
		Document document = getSourceDocument(controller);

		// controller must be not null
		assertNotNull(document);

		// get doctype
		DocumentType documentType = document.getDoctype();

		// documentType must be not null
		assertNotNull(documentType);

		int start = ((IDOMNode) documentType).getStartOffset();
		int end = ((IDOMNode) documentType).getEndOffset();

		// get editor control
		StyledText styledText = part.getSourceEditor().getTextViewer()
				.getTextWidget();
		assertNotNull(styledText);
		styledText.replaceTextRange(start, end - start, "");

		TestUtil.delay(500);

		controller.visualRefresh();

		TestUtil.delay(500);

		nsIDOMDocument visualDocument = TestUtil.getVpeVisualDocument(part);

		nsIDOMElement element = visualDocument.getElementById(ELEMENT_ID);
		assertNotNull(element);

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

}
