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

package org.jboss.tools.jsf.vpe.richfaces.test.jbide;

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.richfaces.test.RichFacesAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * 
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class Jbide1614Test extends VpeTest {

	public static final String FILE_NAME1 = "JBIDE/1614/JBIDE-1614-absolute.xhtml";
    public static final String FILE_NAME2 = "JBIDE/1614/JBIDE-1614-related.xhtml";

    public Jbide1614Test(String name) {
	super(name);
    }

    public void testAbsolutePath() throws Throwable {

	// wait
	TestUtil.waitForJobs();
	// set exception
	setException(null);

	// get test page path
	IFile file = (IFile) TestUtil.getComponentPath(FILE_NAME1,
			RichFacesAllTests.IMPORT_PROJECT_NAME);

	IEditorInput input = new FileEditorInput(file);

	// open and get editor
	JSPMultiPageEditor part = openEditor(input);

	// get dom document
	nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
	assertNotNull(document);

	// get dom element
	nsIDOMElement element = document.getDocumentElement();
	assertNotNull(element);

	// get root node
	nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "code" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_CODE);

	Assert.assertEquals(1, elements.size());

	// check exception
	if (getException() != null) {
	    throw getException();
	}

    }

    public void testRelatedPath() throws Throwable {

	// wait
	TestUtil.waitForJobs();
	// set exception
	setException(null);

	// get test page path
	IFile file = (IFile) TestUtil.getComponentPath(FILE_NAME2,
			RichFacesAllTests.IMPORT_PROJECT_NAME);

	IEditorInput input = new FileEditorInput(file);

	// open and get editor
	JSPMultiPageEditor part = openEditor(input);

	// get dom document
	nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
	assertNotNull(document);

	// get dom element
	nsIDOMElement element = document.getDocumentElement();
	assertNotNull(element);

	// get root node
	nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "code" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_CODE);

	Assert.assertEquals(1, elements.size());

	// check exception
	if (getException() != null) {
	    throw getException();
	}

    }

}
