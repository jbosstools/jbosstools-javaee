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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNodeList;

/**
 * The class for testing JBIDE-3734
 * 
 * @author yradtsevich
 */
public class JBIDE3734Test extends VpeTest {
	private static final String TEST_FILE_NAME = "JBIDE3734.xhtml"; //$NON-NLS-1$
	private static final String CSS_FILE_NAME = "page.css"; //$NON-NLS-1$
	private final String TEST_FOLDER_PATH = "JBIDE/3734/"; //$NON-NLS-1$
	public JBIDE3734Test(String name) {
		super(name);
	}

	/**
	 * Tests if attributes of a4j:page are rendered to visual BODY element. 
	 * 
	 * @throws Throwable
	 */
	public void testBodyAttributes() throws Throwable {
		setException(null);
        VpeController vpeController = openTestPage();
        nsIDOMElement body = vpeController.getVisualBuilder().getContentArea();

        assertEquals("bold", body.getAttribute(HTML.ATTR_CLASS)); //$NON-NLS-1$
        assertEquals("background-color: black;", body.getAttribute(HTML.ATTR_STYLE)); //$NON-NLS-1$

		if(getException()!=null) {
			throw getException();
		}
	}
	
	/**
	 * Tests if CSS link from a4j:page head facet is linked to the page. 
	 * 
	 * @throws Throwable
	 */
	public void testLink() throws Throwable {
		setException(null);
        VpeController vpeController = openTestPage();
        nsIDOMElement head = (nsIDOMElement) vpeController
        	.getVisualBuilder()
        	.getHeadNode()
        	.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

        nsIDOMNodeList links = head.getElementsByTagName(HTML.TAG_STYLE);
        boolean pageCssLinkFound = false;
        for (int i = 0; i < links.getLength(); i++) {
        	final String href = ((nsIDOMElement) links.item(i)
        			.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID))
        			.getAttribute(HTML.ATTR_HREF);
        	if (href != null && href.contains(TEST_FOLDER_PATH + CSS_FILE_NAME)) {
        		pageCssLinkFound = true;
        		break;
        	}
        }
        
        assertTrue("CSS link to 'page.css' is not found", pageCssLinkFound); //$NON-NLS-1$

		if(getException()!=null) {
			throw getException();
		}
	}

	private VpeController openTestPage() throws CoreException,
			PartInitException {
		IFile ifile = (IFile) TestUtil.getComponentPath(TEST_FOLDER_PATH + TEST_FILE_NAME,
        		JsfAllTests.IMPORT_PROJECT_NAME);
        IEditorInput input = new FileEditorInput(ifile);
        JSPMultiPageEditor part = openEditor(input);
        VpeController vpeController = TestUtil.getVpeController(part);
		return vpeController;
	}
}
