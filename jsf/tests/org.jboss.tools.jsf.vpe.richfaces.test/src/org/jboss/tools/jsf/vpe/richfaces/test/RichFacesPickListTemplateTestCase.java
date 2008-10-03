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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;


/**
 * Test case for testing <rich:pickList/> component.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesPickListTemplateTestCase extends VpeTest {

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
    public void testSimplePickList() {
        nsIDOMElement rst;
        try {
            rst = TestUtil.performTestForRichFacesComponent((IFile) TestUtil.getComponentPath("components/pickList/pickList.xhtml", //$NON-NLS-1$
            		RichFacesAllTests.IMPORT_PROJECT_NAME));
            final List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

            TestUtil.findAllElementsByName(rst, elements, HTML.TAG_TABLE);
            assertEquals("Count of tables should be 3", 3, elements.size()); //$NON-NLS-1$
            nsIDOMElement tableOne = (nsIDOMElement) elements.get(0).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

            assertEquals("Style class should be equals", "rich-list-picklist", tableOne.getAttribute(HTML.ATTR_CLASS)); //$NON-NLS-1$ //$NON-NLS-2$
            assertEquals("Style should be empty", "", tableOne.getAttribute(HTML.ATTR_STYLE)); //$NON-NLS-1$ //$NON-NLS-2$
            elements.clear();
            TestUtil.findAllElementsByName(rst, elements, HTML.TAG_DIV);
            assertEquals("Count of divs should be 18", 18, elements.size()); //$NON-NLS-1$

            elements.clear();
            TestUtil.findAllElementsByName(rst, elements, HTML.TAG_IMG);
            assertEquals("Count of divs should be 18", 4, elements.size()); //$NON-NLS-1$

        } catch (CoreException e) {
            TestUtil.fail(e);
        } catch (Throwable e) {
            TestUtil.fail(e);
        }

    }

}
