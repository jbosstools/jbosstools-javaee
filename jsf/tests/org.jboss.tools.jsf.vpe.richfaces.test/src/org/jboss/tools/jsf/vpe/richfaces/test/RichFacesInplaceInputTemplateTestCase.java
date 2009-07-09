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
import org.jboss.tools.jst.jsp.outline.cssdialog.common.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;


/**
 * Test case for testing <rich:inplaceInput/> template.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesInplaceInputTemplateTestCase extends VpeTest {

    /** The Constant EL_VALUE. */
    private static final String EL_VALUE = "#{person.name}"; //$NON-NLS-1$

    /** The Constant MY_STYLE_CLASS. */
    private static final String MY_STYLE_CLASS = "myStyleClass"; //$NON-NLS-1$

    /** The Constant RICH_INPLACE_VIEW. */
    private static final String RICH_INPLACE_VIEW = "rich-inplace rich-inplace-view"; //$NON-NLS-1$

    /** The Constant TEMPLATE_WITH_EMPTY_TAG. */
    private static final String TEMPLATE_WITH_EMPTY_TAG = "components/inplaceInput/inplaceInput.xhtml"; //$NON-NLS-1$

    /** The Constant TEMPLATE_WITH_VALUE_AND_STYLE_CLASS. */
    private static final String TEMPLATE_WITH_VALUE_AND_STYLE_CLASS = "components/inplaceInput/inplaceInputWithStyleClassAttribute.xhtml"; //$NON-NLS-1$

    /** The Constant TEMPLATE_WITH_VALUE_ATTR. */
    private static final String TEMPLATE_WITH_VALUE_ATTR = "components/inplaceInput/inplaceInputWithValueAttribute.xhtml"; //$NON-NLS-1$
    
    private static final String EMPTY_INPUT_STRING = "\u00A0\u00A0\u00A0"; //$NON-NLS-1$

    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public RichFacesInplaceInputTemplateTestCase(String name) {
        super(name);
    }

    /**
     * Base check.
     * 
     * @param styleClass the style class
     * @param value the value
     * @param page the page
     * 
     * @throws CoreException the core exception
     * @throws Throwable the throwable
     */
    private void baseCheck(String page, String value, String styleClass) throws Throwable, CoreException {
        final nsIDOMElement rst = TestUtil.performTestForRichFacesComponent((IFile) TestUtil.getComponentPath(page,
        		RichFacesAllTests.IMPORT_PROJECT_NAME));

        List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

        TestUtil.findAllElementsByName(rst, elements, HTML.TAG_SPAN);

        assertEquals("Count of items should be 1", 1, elements.size()); //$NON-NLS-1$

        final nsIDOMElement element = (nsIDOMElement) elements.get(0).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
        
        String first = "null"; //$NON-NLS-1$
        String second = "null"; //$NON-NLS-1$
        
        if(value!=null){
            first = value;
        }
        
        if(element.getFirstChild().getNodeValue()!=null){
            second = element.getFirstChild().getNodeValue().trim();
        }
        
        assertEquals("Text value  should be empty string", first, second); //$NON-NLS-1$

        assertTrue("Style class should be equals " + styleClass, element.getAttribute(HTML.ATTR_CLASS).contains(styleClass)); //$NON-NLS-1$
    }

    /**
     * Test inplace input without attributes.
     * 
     * @throws CoreException the core exception
     * @throws Throwable the throwable
     */
    public void testInplaceInputWithoutAttributes() throws CoreException, Throwable {
        baseCheck(TEMPLATE_WITH_EMPTY_TAG, EMPTY_INPUT_STRING, RICH_INPLACE_VIEW);

    }

    /**
     * Test inplace input with value.
     * 
     * @throws CoreException the core exception
     * @throws Throwable the throwable
     */
    public void testInplaceInputWithValue() throws CoreException, Throwable {
        baseCheck(TEMPLATE_WITH_VALUE_ATTR, EL_VALUE, RICH_INPLACE_VIEW);

    }

    /**
     * Test inplace input with value and style.
     * 
     * @throws CoreException the core exception
     * @throws Throwable the throwable
     */
    public void testInplaceInputWithValueAndStyle() throws CoreException, Throwable {
        baseCheck(TEMPLATE_WITH_VALUE_AND_STYLE_CLASS, EL_VALUE, MY_STYLE_CLASS);

    }

}
