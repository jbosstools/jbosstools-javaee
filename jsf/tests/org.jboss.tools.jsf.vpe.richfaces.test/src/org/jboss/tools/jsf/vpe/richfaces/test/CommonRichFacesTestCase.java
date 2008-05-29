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


import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.VpeEditorPart;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.jboss.tools.vpe.xulrunner.editor.XulRunnerEditor;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;


/**
 * The Class CommonRichFacesTestCase.
 * 
 * @author Eugene Stherbin
 */
public abstract class CommonRichFacesTestCase extends VpeTest {
    
    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public CommonRichFacesTestCase(String name) {
        super(name);
        setCheckWarning(false);
    }

    /**
     * get xulrunner source page.
     * 
     * @param part - JSPMultiPageEditor
     * 
     * @return nsIDOMDocument
     */
    protected nsIDOMDocument getVpeVisualDocument(JSPMultiPageEditor part) {

        VpeEditorPart visualEditor = (VpeEditorPart) part.getVisualEditor();

        VpeController vpeController = visualEditor.getController();

        // get xulRunner editor
        XulRunnerEditor xulRunnerEditor = vpeController.getXulRunnerEditor();

        // get dom document
        nsIDOMDocument document = xulRunnerEditor.getDOMDocument();

        return document;
    }

    /**
     * Perform test for rich faces component.
     * 
     * @param componentPage the component page
     * 
     * @return the ns IDOM element
     * 
     * @throws Throwable the throwable
     */
    protected nsIDOMElement performTestForRichFacesComponent(IFile componentPage) throws Throwable {
        nsIDOMElement rst = null;
        TestUtil.waitForJobs();

        setException(null);

        // IFile file = (IFile)
        // TestUtil.getComponentPath(componentPage,getImportProjectName());
        IEditorInput input = new FileEditorInput(componentPage);

        TestUtil.waitForJobs();
        //
        JSPMultiPageEditor editor = (JSPMultiPageEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                input, EDITOR_ID, true);

        // get dom document
        nsIDOMDocument document = getVpeVisualDocument(editor);
        rst = document.getDocumentElement();
        // check that element is not null
        assertNotNull(rst);
        return rst;
    }
    
    void fail(Throwable t){
        fail("Test case was fail "+t.getMessage()+":"+t);
    }
}
