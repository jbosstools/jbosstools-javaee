/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.test;

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;


/**
 * Test case for testing <rich:pickList/> component.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesPickListTemplateTestCase extends VpeTest {
	private static final String FILE_NAME = "components/pickList/pickList.xhtml";
	private static final String TEST_ELEMENT_ID = "testElement";

    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public RichFacesPickListTemplateTestCase(String name) {
        super(name);
    }

    /**
     * Test simple pick list.
     */
    public void testSimplePickList() throws Throwable {

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

    		nsIDOMElement element = findElementById(controller, TEST_ELEMENT_ID);
    		assertNotNull(element);
            
            final List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

            TestUtil.findAllElementsByName(element, elements, HTML.TAG_TABLE);
            assertEquals("Count of tables should be 3", 3, elements.size()); //$NON-NLS-1$
            nsIDOMElement tableOne = queryInterface(elements.get(0), nsIDOMElement.class);

            assertEquals("Style class should be equals", "rich-list-picklist", tableOne.getAttribute(HTML.ATTR_CLASS)); //$NON-NLS-1$ //$NON-NLS-2$
            assertEquals("Style should be empty", "", tableOne.getAttribute(HTML.ATTR_STYLE)); //$NON-NLS-1$ //$NON-NLS-2$
            elements.clear();
            TestUtil.findAllElementsByName(element, elements, HTML.TAG_DIV);
            assertEquals("Count of divs should be 15", 15, elements.size()); //$NON-NLS-1$

            elements.clear();
            TestUtil.findAllElementsByName(element, elements, HTML.TAG_IMG);
            assertEquals("Count of img should be 4", 4, elements.size()); //$NON-NLS-1$
    }

}
