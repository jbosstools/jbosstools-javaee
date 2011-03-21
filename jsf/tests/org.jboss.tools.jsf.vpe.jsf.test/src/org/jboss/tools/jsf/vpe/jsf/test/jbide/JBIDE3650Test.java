/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.jst.jsp.util.NodesManagingUtil;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.TextUtil;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;

/**
 * junit for https://jira.jboss.org/jira/browse/JBIDE-3650
 * @author mareshkau
 *
 */
public class JBIDE3650Test extends VpeTest {

	public JBIDE3650Test(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setException(null);
	}

	@Override
	protected void tearDown() throws Exception {
		if(getException()!=null) {
			throw new Exception(getException());
		}
		super.tearDown();
	}
	
	public void testJBIDE3650inTextNodes() throws Exception {
		testSelection("JBIDE/3650/jbide3650Test.xhtml", null, 12, 7, 17); //$NON-NLS-1$
	}
	
	public void testJBIDE3650inAttributNodes() throws Exception {
		testSelection("JBIDE/3650/jbide3650Test.xhtml", "value", 7, 13, 43);  //$NON-NLS-1$//$NON-NLS-2$
	}

	private void testSelection(String pageName, String attributeName, int expectedVisualPosition, int srcLine, int srcLinePos) throws Exception{
        IFile ifile = (IFile) TestUtil.getComponentPath(pageName, 
        		JsfAllTests.IMPORT_PROJECT_NAME);
        IEditorInput input = new FileEditorInput(ifile);
        JSPMultiPageEditor part = openEditor(input);
        //wait for initialization of editor
        VpeController vpeController = TestUtil.getVpeController(part);
        int focusOffcetInSourceDocument = TestUtil.getLinePositionOffcet(part.getSourceEditor().getTextViewer(), srcLine, srcLinePos);
        part.getSourceEditor().getTextViewer().getTextWidget().setCaretOffset(focusOffcetInSourceDocument);
        vpeController.getPageContext().getSourceBuilder().getStructuredTextViewer().setSelectedRange(focusOffcetInSourceDocument, 0);
        nsIDOMNode lastSelectedNode = getSelectedNode(vpeController.getXulRunnerEditor());
        NodeImpl sourceSelectedNode=null;
        //was selected source el
        if(attributeName!=null) {
	        Element sourceSelectedElement =  (Element) vpeController.getDomMapping().getNearSourceNode(lastSelectedNode);
			 sourceSelectedNode = (NodeImpl) sourceSelectedElement.getAttributeNode(attributeName);
        } else {
           sourceSelectedNode = (NodeImpl) vpeController.getDomMapping().getNearSourceNode(lastSelectedNode);
        }
		int focusOffcetReferenceToSourceNode = focusOffcetInSourceDocument
		- NodesManagingUtil.getStartOffsetNode(sourceSelectedNode);
        int visualPosition = TextUtil.visualPosition(sourceSelectedNode.getValueSource(), focusOffcetReferenceToSourceNode);
        assertEquals("Visual Position should be ", expectedVisualPosition , visualPosition); //$NON-NLS-1$
        int calculatedSourcePositon = TextUtil.sourcePosition(sourceSelectedNode.getValueSource(), lastSelectedNode.getNodeValue(), visualPosition); 
        assertEquals("calculated Source position and visual position should be equals", calculatedSourcePositon,focusOffcetReferenceToSourceNode);     	 //$NON-NLS-1$
        closeEditors();	
	}
}
