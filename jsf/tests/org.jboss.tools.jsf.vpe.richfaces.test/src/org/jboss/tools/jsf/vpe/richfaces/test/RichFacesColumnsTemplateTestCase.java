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


import static org.jboss.tools.vpe.ui.test.TestUtil.performTestForRichFacesComponent;
import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

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
 * Test case for testing <rich:columns>.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesColumnsTemplateTestCase extends VpeTest {
    /** The Constant COLUMNS_WITH_ATTRIBUTES. */
    private static final String COLUMNS_WITH_ATTRIBUTES = "components/columns/columnsWithAttributes.xhtml";

    /** The Constant COMPONENTS_COLUMNS_COLUMNS_XHTML. */
    private static final String COMPONENTS_COLUMNS_COLUMNS_XHTML = "components/columns/columns.xhtml";

    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public RichFacesColumnsTemplateTestCase(String name) {
        super(name);
    }

    /**
     * Test columns with attributes.
     */
    public void testColumnsWithAttributes() {
        try {
            final nsIDOMElement rst = performTestForRichFacesComponent((IFile) TestUtil.getComponentPath(COLUMNS_WITH_ATTRIBUTES,
            		RichFacesAllTests.IMPORT_PROJECT_NAME));

            final List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();
            TestUtil.findAllElementsByName(rst, elements, HTML.TAG_TD);

            nsIDOMElement divOne = queryInterface(elements.get(0), nsIDOMElement.class);

            assertEquals("Style class should be equals", "dr-table-cell rich-table-cell myClass", divOne.getAttribute(HTML.ATTR_CLASS));
            assertEquals("Style should be equals ", "text-align: center;", divOne.getAttribute(HTML.ATTR_STYLE));
            assertTrue("Style should contains of 52 value ", divOne.getAttribute("width").contains("52"));
        } catch (CoreException e) {
            fail(e.getMessage() + ":" + e);
        } catch (Throwable e) {
            fail(e.getMessage() + ":" + e);
        }
    }

    /**
     * Test simple columns.
     */
    public void testSimpleColumns() {
        try {
            final nsIDOMElement rst = performTestForRichFacesComponent((IFile) TestUtil.getComponentPath(COMPONENTS_COLUMNS_COLUMNS_XHTML,
            		RichFacesAllTests.IMPORT_PROJECT_NAME));

            final List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();
            TestUtil.findAllElementsByName(rst, elements, HTML.TAG_TD);

            nsIDOMElement divOne = queryInterface(elements.get(0), nsIDOMElement.class);

            assertEquals("Style class should be equals", "dr-table-cell rich-table-cell", divOne.getAttribute(HTML.ATTR_CLASS));
        } catch (CoreException e) {
            fail(e.getMessage() + ":" + e);
        } catch (Throwable e) {
            fail(e.getMessage() + ":" + e);
        }
    }
}
