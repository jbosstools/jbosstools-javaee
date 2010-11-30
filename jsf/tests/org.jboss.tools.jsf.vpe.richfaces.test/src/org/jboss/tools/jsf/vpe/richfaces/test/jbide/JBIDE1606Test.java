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

import org.eclipse.core.resources.IFile;
import org.jboss.tools.jsf.vpe.richfaces.test.RichFacesAllTests;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;


/**
 * The Class JBIDE1606Test.
 * 
 * @author Evgenij Stherbin
 */
public class JBIDE1606Test extends VpeTest {

    /** The Constant HELLO. */
    private static final String HELLO = "Hello"; //$NON-NLS-1$

    /** The Constant PAGE. */
    private static final String PAGE = "components/dropDownMenu/jbide1606.xhtml"; //$NON-NLS-1$

    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public JBIDE1606Test(String name) {
        super(name);
    }

    /**
     * Test JBID e1606.
     * 
     * @throws Throwable the throwable
     */
    public void testSimpleJBIDE1606() throws Throwable {
        performTestForVpeComponent((IFile) TestUtil.getComponentPath(PAGE, RichFacesAllTests.IMPORT_PROJECT_NAME));
    }

    /**
     * Test JBID e1606.
     * 
     * @throws Throwable the throwable
     */
    public void testJBIDE1606() throws Throwable {
        final nsIDOMElement rst = TestUtil.performTestForRichFacesComponent((IFile) TestUtil.getComponentPath(PAGE,
        		RichFacesAllTests.IMPORT_PROJECT_NAME));

        List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

        // DOMTreeDumper dumper = new DOMTreeDumper();
        // dumper.dumpToStream(System.err, rst);
        TestUtil.findAllElementsByName(rst, elements, HTML.TAG_SPAN);
        
        assertTrue("Size of span's should be gt that 0", elements.size() > 0); //$NON-NLS-1$
        
        nsIDOMElement element = queryInterface(elements.get(0), nsIDOMElement.class);
        
        assertEquals("Test should be equals "+HELLO,HELLO,element.getFirstChild().getNodeValue()); //$NON-NLS-1$
    
        
    }
}
