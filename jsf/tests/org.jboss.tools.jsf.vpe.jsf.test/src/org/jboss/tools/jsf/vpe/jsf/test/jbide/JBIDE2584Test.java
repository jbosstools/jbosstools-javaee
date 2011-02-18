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
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.mapping.VpeElementMapping;
import org.jboss.tools.vpe.editor.template.VpeTemplate;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Node;



/**
 * @author mareshkau
 * Test class which tests text template
 */
public class JBIDE2584Test extends VpeTest {

	public JBIDE2584Test(String name) {
		super(name);
	}
	
	public void testForSimpleText() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		// Tests CA
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/2584/text.xhtml", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file " + "JBIDE/2584/text.xhtml", file); //$NON-NLS-1$ //$NON-NLS-2$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		ITextViewer itextViewer = part.getSourceEditor().getTextViewer();
		
		 
		Node simpleTextNode =  TestUtil.getNodeMappingBySourcePosition(itextViewer, 14, 10);
		
		VpeController vpeController = TestUtil.getVpeController(part);

		VpeDomMapping domMapping = vpeController.getDomMapping();
		
		VpeElementMapping simpleTextMapping = (VpeElementMapping) domMapping.getNearNodeMapping(simpleTextNode);
	
		VpeTemplate simpleTextTemplate = simpleTextMapping.getTemplate();
		
		int positionOffset = TestUtil.getLinePositionOffcet(itextViewer, 14, 10);
		nsIDOMNode domNode = simpleTextTemplate.getVisualNodeBySourcePosition(simpleTextMapping, new Point(positionOffset, -positionOffset), domMapping).getFirstChild();
	
		assertEquals(simpleTextNode ,domMapping.getNearElementMappingAtVisualNode(domNode).getSourceNode());
		assertEquals("Node should be a text node", nsIDOMNode.TEXT_NODE,domNode.getNodeType()); //$NON-NLS-1$
		
		assertEquals(simpleTextNode.getNodeValue(), domNode.getNodeValue());
	}
	
	public void testForElText() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		// Tests CA
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/2584/text.xhtml", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file " + "JBIDE/2584/text.xhtml", file); //$NON-NLS-1$ //$NON-NLS-2$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		ITextViewer itextViewer = part.getSourceEditor().getTextViewer();
		 
		Node simpleTextNode =  TestUtil.getNodeMappingBySourcePosition(itextViewer, 15, 27);
		
		VpeController vpeController = TestUtil.getVpeController(part);

		VpeDomMapping domMapping = vpeController.getDomMapping();
		
		VpeElementMapping simpleTextMapping = (VpeElementMapping) domMapping.getNearNodeMapping(simpleTextNode);
	
		VpeTemplate simpleTextTemplate = simpleTextMapping.getTemplate();
		
		int positionOffset = TestUtil.getLinePositionOffcet(itextViewer, 15, 27);
		nsIDOMNode domNode = simpleTextTemplate.getVisualNodeBySourcePosition(simpleTextMapping, new Point(positionOffset, -positionOffset), domMapping).getFirstChild();
	
		assertEquals("Node should be a text node", nsIDOMNode.TEXT_NODE,domNode.getNodeType()); //$NON-NLS-1$
		assertEquals(simpleTextNode ,domMapping.getNearElementMappingAtVisualNode(domNode).getSourceNode());
		assertEquals("Hello", domNode.getNodeValue().trim()); //$NON-NLS-1$
	}
	
}
