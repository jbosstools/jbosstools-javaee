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
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.util.DocTypeUtil;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentType;

/**
 * 
 * @author sdzmitrovich
 * 
 *         test for http://jira.jboss.com/jira/browse/JBIDE-1718
 * 
 */
public class JsfJbide2170Test extends VpeTest {

	public static final String IMPORT_PROJECT_NAME = "jsfTest"; //$NON-NLS-1$

	private static final String WITHOUT_DOCTYPE_TEST_PAGE_NAME = "JBIDE/2170/there_is_not_doctype.xhtml"; //$NON-NLS-1$

	private static final String WITH_DOCTYPE_TEST_PAGE_NAME = "JBIDE/2170/there_is_doctype.xhtml"; //$NON-NLS-1$

	private static final String TEMPLATE_DOCTYPE_TEST_PAGE_NAME = "JBIDE/2170/template_doctype_test.xhtml"; //$NON-NLS-1$

	private static final String COMPLEX_DOCTYPE_TEST_PAGE_NAME = "JBIDE/2170/complex_doctype_test.xhtml"; //$NON-NLS-1$

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
		IFile file = (IFile) TestUtil.getComponentPath(
				WITHOUT_DOCTYPE_TEST_PAGE_NAME, IMPORT_PROJECT_NAME);

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
		nsIDOMDocument document = getVpeVisualDocument(part);
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
				WITH_DOCTYPE_TEST_PAGE_NAME, IMPORT_PROJECT_NAME);

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
		nsIDOMDocument document = getVpeVisualDocument(part);
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
				TEMPLATE_DOCTYPE_TEST_PAGE_NAME, IMPORT_PROJECT_NAME);

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
		nsIDOMDocument document = getVpeVisualDocument(part);
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
		IFile file = (IFile) TestUtil.getComponentPath(
				COMPLEX_DOCTYPE_TEST_PAGE_NAME, IMPORT_PROJECT_NAME);

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
		nsIDOMDocument document = getVpeVisualDocument(part);
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

}
