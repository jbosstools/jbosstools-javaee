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
import org.eclipse.ui.PartInitException;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;


/**
 * Test case for testing {@link RichFacesComboBoxTemplateTestCase} class.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesComboBoxTemplateTestCase extends VpeTest {

    /** The Constant _150PX. */
    private static final String _150PX = "150px"; //$NON-NLS-1$

    /** The Constant _250PX. */
    private static final String _250PX = "250px"; //$NON-NLS-1$

    /** The Constant _255PX. */
    private static final String _255PX = "255px"; //$NON-NLS-1$

    /** The Constant COMPONENTS_COMBO_BOX_WITHOUT_ATTR. */
    private static final String COMPONENTS_COMBO_BOX_WITHOUT_ATTR = "components/comboBox/comboBox.xhtml"; //$NON-NLS-1$

    /** The Constant COMPONENTS_COMBO_WITH_ATTR_TEMPLATE. */
    private static final String COMPONENTS_COMBO_WITH_ATTR_TEMPLATE = "components/comboBox/comboBoxWithAttributes.xhtml"; //$NON-NLS-1$

    /** The Constant COMPONENTS_COMBO_WITH_ATTR_TEMPLATE2. */
    private static final String COMPONENTS_COMBO_WITH_ATTR_TEMPLATE2 = "components/comboBox/comboBoxWithAttributes2.xhtml"; //$NON-NLS-1$

    /** DEFAULT_INPUT_STYLE. */
    private static final String DEFAULT_INPUT_STYLE = "rich-combobox-font-disabled rich-combobox-input-inactive"; //$NON-NLS-1$

    /** The Constant DEFAULT_WIDTH. */
    private static final String DEFAULT_WIDTH = "width: 150px;"; //$NON-NLS-1$

    /** The Constant EL_VALUE. */
    private static final String EL_VALUE = "#{bean.value}"; //$NON-NLS-1$

    /** The Constant SELECT_ANY_VALUE. */
    private static final String SELECT_ANY_VALUE = "Select Any Value"; //$NON-NLS-1$

    /** The Constant ZERO. */
    private static final int ZERO = 0;

    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public RichFacesComboBoxTemplateTestCase(String name) {
        super(name);
    }

    /**
     * Base table check.
     * 
     * @param width the width
     * @param page the page
     * 
     * @return the ns IDOM element
     * 
     * @throws Throwable the throwable
     * @throws PartInitException the part init exception
     */
    private nsIDOMElement baseTableCheck(String page, String width) throws PartInitException, Throwable {
        final nsIDOMElement rst = TestUtil.performTestForRichFacesComponent((IFile) TestUtil.getComponentPath(page,
        		RichFacesAllTests.IMPORT_PROJECT_NAME));

        List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

        // find "td" elements

        TestUtil.findAllElementsByName(rst, elements, HTML.TAG_DIV);

        nsIDOMElement divOne = queryInterface(elements.get(5), nsIDOMElement.class);
        assertTrue("Style classes should be contains ",divOne.getAttribute(HTML.ATTR_CLASS).contains("rich-combobox-font rich-combobox")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Default style should be contains " + width, divOne.getAttribute(HTML.ATTR_STYLE).indexOf(width) > 1); //$NON-NLS-1$
        assertTrue("Default style should be contains " + width, divOne.getAttribute(HTML.ATTR_STYLE).contains("width")); //$NON-NLS-1$ //$NON-NLS-2$
        // Check input
        return rst;
    }

    /**
     * Check value in input.
     * 
     * @param inputValue the input value
     * @param rst the rst
     */
    private void checkValueInInput(final nsIDOMElement rst, String inputValue) {
        final List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

        TestUtil.findAllElementsByName(rst, elements, HTML.TAG_INPUT);

        assertEquals("Size should be equals 2", 3, elements.size()); //$NON-NLS-1$
        final nsIDOMElement input = queryInterface(elements.get(0), nsIDOMElement.class);
        assertEquals("Value should be equals " + inputValue, inputValue, input.getAttribute(HTML.ATTR_VALUE)); //$NON-NLS-1$
    }

    /**
     * Test combo box with attributes.
     * 
     * @throws Throwable the throwable
     * @throws PartInitException the part init exception
     */
    public void testComboBoxWithAttributes() throws PartInitException, Throwable {
        final nsIDOMElement rst = baseTableCheck(COMPONENTS_COMBO_WITH_ATTR_TEMPLATE, _250PX);
        
        checkValueInInput(rst, SELECT_ANY_VALUE);
    }

    /**
     * Test combo box with attributes2.
     * 
     * @throws Throwable the throwable
     * @throws PartInitException the part init exception
     */
    public void testComboBoxWithAttributes2() throws PartInitException, Throwable {
        final nsIDOMElement rst = baseTableCheck(COMPONENTS_COMBO_WITH_ATTR_TEMPLATE2, _255PX);

        checkValueInInput(rst, EL_VALUE);

    }

    /**
     * Test combo box.
     * 
     * @throws PartInitException the part init exception
     * @throws Throwable the throwable
     */
    public void testComboBoxWithoutAttributes() throws PartInitException, Throwable {

        final nsIDOMElement rst = baseTableCheck(COMPONENTS_COMBO_BOX_WITHOUT_ATTR, _150PX);
        final List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

        TestUtil.findAllElementsByName(rst, elements, HTML.TAG_INPUT);

        assertEquals("Size should be equals 2", 3, elements.size()); //$NON-NLS-1$

        final nsIDOMElement input = queryInterface(elements.get(0), nsIDOMElement.class);

        assertEquals("Default input class should be equals " + DEFAULT_INPUT_STYLE, input.getAttribute(HTML.ATTR_CLASS), //$NON-NLS-1$
                DEFAULT_INPUT_STYLE);
//        assertEquals("Input style style should be empty", "", input.getAttribute(HTML.ATTR_STYLE));
//        assertEquals("Input type should be text", HTML.VALUE_TEXT_TYPE, input.getAttribute(HTML.ATTR_TYPE));
//        assertEquals("Input size should be " + String.valueOf(10), String.valueOf(10), input.getAttribute(HTML.ATTR_SIZE));
//
//        final nsIDOMElement img = queryInterface(elements.get(1), nsIDOMElement.class);
//        assertTrue("Shoul contains of image path ", img.getAttribute("src").indexOf("\\comboBox\\down.gif") > 1);

    }

}
