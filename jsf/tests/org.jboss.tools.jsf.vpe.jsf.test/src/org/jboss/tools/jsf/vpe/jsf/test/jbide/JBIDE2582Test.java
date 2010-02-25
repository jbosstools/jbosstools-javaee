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


package org.jboss.tools.jsf.vpe.jsf.test.jbide;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.jsf.vpe.jsf.test.CommonJBIDE2010Test;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.mapping.VpeNodeMapping;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.SelectionUtil;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.xulrunner.editor.XulRunnerEditor;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Node;


/**
 * Test case for testing <a
 * href="https://jira.jboss.org/jira/browse/JBIDE-2582"> JBIDE-2582 </a> issue
 * 
 * @author Evgenij Stherbin
 */
public class JBIDE2582Test extends CommonJBIDE2010Test {

    /** The Constant DIR_TEST_PAGE_NAME_3. */
    protected static final String PAGE_1 = "JBIDE/2582/page1.xhtml"; //$NON-NLS-1$

    /** The Constant DIR_TEST_PAGE_NAME_3. */
    protected static final String PAGE_2 = "JBIDE/2582/page2.xhtml"; //$NON-NLS-1$

    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public JBIDE2582Test(String name) {
        super(name);

    }

    /**
     * Test rs substitution.
     * 
     * @throws Throwable the throwable
     */
    public void _testRsSubstitution() throws Throwable {
        final nsIDOMElement rst = TestUtil.performTestForRichFacesComponent(file);

        assertNotNull(rst);

        final List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

        TestUtil.findAllElementsByName(rst, elements, HTML.TAG_SPAN);

        assertEquals("Size should be equals 1", 1, elements.size()); //$NON-NLS-1$

        final nsIDOMElement spanOne = (nsIDOMElement) elements.get(0).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

        assertEquals("Style attribute should be substituted", "Hello", spanOne.getFirstChild().getNodeValue()); //$NON-NLS-1$ //$NON-NLS-2$

    }

    /**
     * _test resource substitution in text.
     * 
     * @throws CoreException the core exception
     * @throws Throwable the throwable
     */
    public void _testResourceSubstitutionInText() throws CoreException, Throwable {
        final nsIDOMElement rst = TestUtil.performTestForRichFacesComponent((IFile) TestUtil.getComponentPath(PAGE_2, getOpenProjectName()));
        final List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();
        // DOMTreeDumper dumper = new DOMTreeDumper();
        // dumper.dumpToStream(System.out, rst);
        TestUtil.findAllElementsByName(rst, elements, "H3"); //$NON-NLS-1$
        assertEquals("Size should be equals 1", 1, elements.size()); //$NON-NLS-1$

        final nsIDOMElement h3one = (nsIDOMElement) elements.get(0).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

        assertEquals("Style attribute should be substituted", "Hello", h3one.getFirstChild().getFirstChild().getNodeValue()); //$NON-NLS-1$ //$NON-NLS-2$

        // There are the label:#{msg.header}f

        TestUtil.findAllElementsByName(rst, elements, "SPAN"); //$NON-NLS-1$
        assertEquals("Size should be equals 1", 4, elements.size()); //$NON-NLS-1$
        final nsIDOMElement pOne = ((nsIDOMElement) elements.get(2).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));

        assertEquals(
                "Style attribute should be substituted", "There are the label:Hello Demo Application", pOne.getFirstChild().getNodeValue()); //$NON-NLS-1$ //$NON-NLS-2$

    }

    /**
     * Test selection with resource string.
     * 
     * @throws CoreException the core exception
     * @throws IOException 
     */
    @SuppressWarnings("restriction")
    public void testSelectionWithResourceString()
    		throws CoreException, IOException {
        IFile lfile = (IFile) TestUtil.getComponentPath(PAGE_2, getOpenProjectName());
        IEditorInput input = new FileEditorInput(lfile);
        // open and get editor
        JSPMultiPageEditor part = openEditor(input);

        // get controller
        VpeController controller = TestUtil.getVpeController(part);
        assertNotNull(controller);

        // get dommapping
        VpeDomMapping domMapping = controller.getDomMapping();

        assertNotNull(domMapping);

        // get source map
        Map<Node, VpeNodeMapping> sourceMap = domMapping.getSourceMap();
        assertNotNull(sourceMap);

        // get collection of VpeNodeMapping
        Collection<VpeNodeMapping> mappings = sourceMap.values();
        assertNotNull(mappings);

        // get xulrunner editor
        XulRunnerEditor xulRunnerEditor = controller.getXulRunnerEditor();
        assertNotNull(xulRunnerEditor);

        int start = controller.getPageContext().getSourceBuilder().getStructuredTextViewer().getTextWidget().getText().indexOf(
                "#{msg.hello_message}"); //$NON-NLS-1$

        assertTrue("Should be gt that 100", start > 100); //$NON-NLS-1$

        IStructuredModel model;
        model = StructuredModelManager.getModelManager()
                .getExistingModelForRead(controller.getSourceEditor().getTextViewer().getDocument());
        IDOMDocument document = null;
        document = ((IDOMModel) model).getDocument();
        ;
        org.w3c.dom.NodeList nodeList = document.getElementsByTagName("h:outputText"); //$NON-NLS-1$

        assertNotNull("Can't be null", nodeList); //$NON-NLS-1$
        assertTrue("Size should be great that 0", nodeList.getLength() > 0); //$NON-NLS-1$

        final Node elementNode = nodeList.item(0);

        SelectionUtil.setSourceSelection(controller.getPageContext(), elementNode, 1, 0);

        nsIDOMNode node = SelectionUtil.getLastSelectedNode(controller.getPageContext());

        assertEquals("Node names should be equals", "Hello", node.getFirstChild().getNodeValue()); //$NON-NLS-1$ //$NON-NLS-2$

    }

    /**
     * Gets the open page name.
     * 
     * @return the open page name
     */
    protected String getOpenPageName() {
        return PAGE_1;
    }

}
