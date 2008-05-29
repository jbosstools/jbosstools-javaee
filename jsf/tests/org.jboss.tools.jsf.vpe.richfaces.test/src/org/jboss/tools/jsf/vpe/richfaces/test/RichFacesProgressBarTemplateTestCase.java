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
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;


/**
 * Test case for testing <rich:progressBar/> component.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesProgressBarTemplateTestCase extends CommonRichFacesTestCase {

    /** The Constant CHECK_BASE_STYLE. */
    private static final String CHECK_BASE_STYLE = "rich-progress-bar-block rich-progress-bar-width rich-progress-bar-shell";

    /** The Constant MY_STYLE_CLASS. */
    private static final String MY_STYLE_CLASS = " myStyleClass";

    /** The Constant PERCENTAGES. */
    private static final String PERCENTAGES = " 60%;";

    /** The Constant SIMPLE_PAGE. */
    private static final String SIMPLE_PAGE = "/components/progressBar/progressBar.xhtml";

    /** The Constant SIMPLE_WITH_ATTRIBUTES. */
    private static final String SIMPLE_WITH_ATTRIBUTES = "/components/progressBar/progressBarWithAttributes.xhtml";

    /** The Constant STYLE_1_FOR_CHECK. */
    private static final String STYLE_1_FOR_CHECK = "width: 250px; text-align: left;";

    /** The Constant STYLE_CLASS_2. */
    private static final String STYLE_CLASS_2 = "rich-progress-bar-height rich-progress-bar-uploaded null";

    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public RichFacesProgressBarTemplateTestCase(String name) {
        super(name);
    }

    /**
     * Base test.
     * 
     * @param styleClass the style class
     * @param page the page
     * @param style the style
     * @param twoStyleClass the two style class
     * @param style2 the style2
     */
    private void baseTest(String page, String styleClass, String style, String twoStyleClass, String style2) {
        nsIDOMElement rst = null;

        try {
            rst = performTestForRichFacesComponent((IFile) TestUtil.getComponentPath(page, RichFacesComponentTest.IMPORT_PROJECT_NAME));
        } catch (CoreException e) {
            fail(e.getMessage() + e);
        } catch (Throwable e) {
            fail(e.getMessage() + e);
        }

        final List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();
        TestUtil.findAllElementsByName(rst, elements, HTML.TAG_DIV);

        assertEquals("Size should be 2", 5, elements.size());
        final nsIDOMElement divOne = (nsIDOMElement) elements.get(3).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

        assertEquals("Style class should be equals" + styleClass, styleClass, divOne.getAttribute(HTML.ATTR_CLASS));
        assertEquals("Style should be equals" + style, style, divOne.getAttribute(HTML.ATTR_STYLE));

        final nsIDOMElement divTwo = (nsIDOMElement) elements.get(4).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

        assertEquals("Style class should be equals" + twoStyleClass, twoStyleClass, divTwo.getAttribute(HTML.ATTR_CLASS));
        assertEquals("Style should be equals" + style2, style2, divTwo.getAttribute(HTML.ATTR_STYLE));

    }

    /**
     * Test simple.
     */
    public void testSimple() {
        baseTest(SIMPLE_PAGE, CHECK_BASE_STYLE, "text-align: left;", STYLE_CLASS_2,VpeStyleUtil.PARAMETER_WIDTH
                + VpeStyleUtil.COLON_STRING + PERCENTAGES);
    }

    /**
     * Test with attributes.
     */
    public void testWithAttributes() {
        baseTest(SIMPLE_WITH_ATTRIBUTES, CHECK_BASE_STYLE + MY_STYLE_CLASS, STYLE_1_FOR_CHECK,
                STYLE_CLASS_2,VpeStyleUtil.PARAMETER_WIDTH
                + VpeStyleUtil.COLON_STRING + PERCENTAGES);
    }

}
