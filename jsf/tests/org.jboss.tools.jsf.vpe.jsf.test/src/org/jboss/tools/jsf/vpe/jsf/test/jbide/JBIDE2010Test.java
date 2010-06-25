/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/


package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.jsf.vpe.jsf.test.CommonJBIDE2010Test;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;


/**
 * Test case for testing https://jira.jboss.com:8443/jira/browse/JBIDE-2010
 * issue.
 * 
 * @author Eugeny Stherbin
 */
public class JBIDE2010Test extends CommonJBIDE2010Test {


    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public JBIDE2010Test(String name) {
        super(name);
    }

    /**
     * Test el template simple.
     * 
     * @throws CoreException the core exception
     * @throws Throwable the throwable
     */
    public void testElTemplateSimple() throws CoreException, Throwable {
        final nsIDOMElement rst = TestUtil.performTestForRichFacesComponent((IFile) TestUtil.getComponentPath(DIR_TEST_PAGE_NAME_2,
                IMPORT_PROJECT_NAME));

        List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

        // find "td" elements

        TestUtil.findAllElementsByName(rst, elements, HTML.TAG_SPAN);

        assertEquals("Count of divs should be equals 1", 1, elements.size()); //$NON-NLS-1$
        final nsIDOMElement spanOne = queryInterface(elements.get(0), nsIDOMElement.class);

        assertEquals("Style attribute should be substituted", VALUE_4, spanOne.getFirstChild().getNodeValue()); //$NON-NLS-1$

    }
    

    /**
     * Test el template simple.
     * 
     * @throws CoreException the core exception
     * @throws Throwable the throwable
     */
    public void testElTemplateSimple2() throws CoreException, Throwable {
        final nsIDOMElement rst = TestUtil.performTestForRichFacesComponent((IFile) TestUtil.getComponentPath(DIR_TEST_PAGE_NAME_3,
                IMPORT_PROJECT_NAME));

        List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

        // find "td" elements

        TestUtil.findAllElementsByName(rst, elements, HTML.TAG_P);

        assertEquals("Value should be equals", 1, elements.size()); //$NON-NLS-1$
        final nsIDOMElement pOne = queryInterface(elements.get(0), nsIDOMElement.class);
       // DOMTreeDumper d = new DOMTreeDumper();
       // d.dumpToStream(System.out, rst);
//        assertEquals("Value should be equals", "Hello "+VALUE_5, pOne.getFirstChild().getFirstChild().getNodeValue());
        assertTrue("Value should contain background style [" + VALUE_4 + "]", //$NON-NLS-1$ //$NON-NLS-2$
        		pOne.getAttribute(HTML.ATTR_STYLE).startsWith(VALUE_4));

    }

}
