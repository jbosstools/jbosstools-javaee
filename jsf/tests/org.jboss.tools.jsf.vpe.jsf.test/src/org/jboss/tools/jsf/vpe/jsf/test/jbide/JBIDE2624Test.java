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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.common.resref.core.ResourceReference;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.resref.core.RelativeFolderReferenceList;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;


/**
 * Test case for testing JBIDE-2624
 * 
 * @author Eugene Stherbin
 * 
 */
public class JBIDE2624Test extends VpeTest {

    private static final String TEST_PAGE_NAME = "JBIDE/2624/greeting.xhtml";

	/**
     * @param name
     */
    public JBIDE2624Test(String name) {
        super(name);
    }

    public void testJBide2624() throws CoreException, IOException {
        // get test page path
        setException(null);
        IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME, //$NON-NLS-1$
                JsfAllTests.IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file. componentPage = " + TEST_PAGE_NAME
				+ ";projectName = " + JsfAllTests.IMPORT_PROJECT_NAME, file);//$NON-NLS-1$
        IEditorInput input = new FileEditorInput(file);

        assertNotNull("Editor input is null", input); //$NON-NLS-1$

        // open and get editor
        JSPMultiPageEditor part = openEditor(input);

        VpeController vpeController = TestUtil.getVpeController(part);

        nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);

        nsIDOMElement element = document.getDocumentElement();

        List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

        TestUtil.findAllElementsByName(element, elements, HTML.TAG_TABLE);

        assertTrue("Faceletes loaded, but shouldn't", elements.size() == 0); //$NON-NLS-1$
        ResourceReference[] resourceReference = new ResourceReference[1];
        resourceReference[0] = new ResourceReference(file.getParent().getLocation().toString() + File.separator + "templates",
                ResourceReference.PROJECT_SCOPE);
        RelativeFolderReferenceList.getInstance().setAllResources(file, resourceReference);
        TestUtil.delay(1000);
        vpeController.visualRefresh();
        TestUtil.delay(1000);
        document = TestUtil.getVpeVisualDocument(part);
        element = document.getDocumentElement();
        elements = new ArrayList<nsIDOMNode>();
        TestUtil.findAllElementsByName(element, elements, HTML.TAG_TABLE);

        assertTrue("Faceletes wasn't loaded from specified path", elements.size() >= 1); //$NON-NLS-1$
        //clear all resources
        RelativeFolderReferenceList.getInstance().setAllResources(file,new ResourceReference[0]);
    }

}
