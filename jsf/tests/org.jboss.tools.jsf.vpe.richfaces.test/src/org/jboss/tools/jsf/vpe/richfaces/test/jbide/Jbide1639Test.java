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
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * 
 * @author sdzmitrovich
 * 
 */
public class Jbide1639Test extends VpeTest {

	public static final String FILE_NAME = "JBIDE/1639/JBIDE-1639.xhtml";
	private static final String TEST_ELEMENT_ID = "testElement";

	public Jbide1639Test(String name) {
		super(name);
	}

	public void testStyleClass() throws Throwable {
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(FILE_NAME,
				RichFacesAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);

		VpeController controller = TestUtil.getVpeController(part);

		nsIDOMElement img = findElementById(controller, TEST_ELEMENT_ID);
		Assert.assertTrue(HTML.TAG_IMG.equalsIgnoreCase(img.getNodeName()));

		nsIDOMNamedNodeMap attributes = img.getAttributes();

		// "img" must have attributes
		assertNotNull(attributes);

		// "img" must have "multiple" attribute
		nsIDOMNode classAttribute = attributes.getNamedItem(HTML.ATTR_CLASS);
		assertNotNull(classAttribute);
		assertNotNull(classAttribute.getNodeValue());
		assertEquals(true, classAttribute.getNodeValue().length() > 0);

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

}
