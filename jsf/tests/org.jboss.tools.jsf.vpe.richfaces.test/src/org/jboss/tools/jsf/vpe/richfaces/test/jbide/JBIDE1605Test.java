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

import org.eclipse.core.resources.IFile;
import org.jboss.tools.jsf.vpe.richfaces.test.RichFacesAllTests;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * <p>
 * Test case for testing
 * <p>
 * <a href="https://jira.jboss.org/jira/browse/JBIDE-1605">
 * https://jira.jboss.org/jira/browse/JBIDE-1605
 * </a> issue.
 * 
 * @author Evgenij Stherbin
 */
public class JBIDE1605Test extends VpeTest {
    
    /** The Constant COMPONENTS_INPLACE_SELECT_INPLACE_SELECT_XHTML. */
    private static final String PAGE = "components/panelMenuGroup/jbide1605.xhtml"; //$NON-NLS-1$
    
    /** The Constant COUNT_OF_DIVS. */
    private static final int COUNT_OF_DIVS = 13;
    
    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public JBIDE1605Test(String name) {
        super(name);
    }
    
    /**
     * Ftest simple JBID e1605.
     * 
     * @throws Throwable the throwable
     */
    public void testSimpleJBIDE1605() throws Throwable {
        performTestForVpeComponent((IFile) TestUtil.getComponentPath(PAGE, RichFacesAllTests.IMPORT_PROJECT_NAME));
    }
    
    /**
     * Test JBID e1605.
     * 
     * @throws Throwable the throwable
     */
    public void testJBIDE1605() throws Throwable {
        final nsIDOMElement rst = TestUtil.performTestForRichFacesComponent((IFile) TestUtil.getComponentPath(PAGE,
        		RichFacesAllTests.IMPORT_PROJECT_NAME));

        List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

//        DOMTreeDumper dumper = new DOMTreeDumper();
//        dumper.dumpToStream(System.err, rst);
        TestUtil.findAllElementsByName(rst, elements, HTML.TAG_DIV);
        assertEquals("Size should be equals",COUNT_OF_DIVS,elements.size());
        
    }
    
    

}
