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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.model.filesystems.impl.SimpleFileImpl;
import org.jboss.tools.common.model.impl.CustomizedObjectImpl;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.SelectionUtil;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Node;


/**
 * 
 * Junit test for JBIDE-4373
 * @author mareshkau
 *
 */
public class JBIDE4373Test extends VpeTest{

	public JBIDE4373Test(String name) {
		super(name);
	}
	
	/**
	 * OpenOn test for Custom elements
	 */
	
	public void testCorrectCustomElements() throws CoreException {

		VpeController vpeController =	openInVpe(JsfAllTests.IMPORT_CUSTOM_FACELETS_PROJECT, "correctCustomTags.xhtml"); //$NON-NLS-1$
		int position = TestUtil.getLinePositionOffcet(vpeController.getSourceEditor().getTextViewer(), 13, 8);
		
		Node sourceNode = SelectionUtil.getNodeBySourcePosition(vpeController.getSourceEditor(), position);
		assertEquals("Name of node should be ma:paginator", "ma:paginator", sourceNode.getNodeName()); //$NON-NLS-1$ //$NON-NLS-2$
		nsIDOMNode domNode = vpeController.getDomMapping().getNearVisualNode(sourceNode);
		vpeController.getSourceBuilder().openOn(domNode);
		IEditorPart  activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		assertEquals("facelets.taglib.xml file should be opened","facelets.taglib.xml", activeEditor.getEditorInput().getName());  //$NON-NLS-1$//$NON-NLS-2$
		StructuredSelection selection = (StructuredSelection) activeEditor.getEditorSite().getSelectionProvider().getSelection();
		CustomizedObjectImpl customizedObjectImpl = (CustomizedObjectImpl) selection.getFirstElement();
		//cheak if selection on right line, how? 
		assertEquals("Children lenght should be ","FileSystems/WEB-ROOT/tags/facelets.taglib.xml/paginator",customizedObjectImpl.getLongPath()); //$NON-NLS-1$ //$NON-NLS-2$
	
	}
	
	/**
	 * test openOn for undefined template
	 */
	public void testIncorrectCustomElements() throws CoreException {
		VpeController vpeController =	openInVpe(JsfAllTests.IMPORT_CUSTOM_FACELETS_PROJECT, "incorrectCustomTags.xhtml"); //$NON-NLS-1$
		int position = TestUtil.getLinePositionOffcet(vpeController.getSourceEditor().getTextViewer(), 9, 6);
		
		Node sourceNode = SelectionUtil.getNodeBySourcePosition(vpeController.getSourceEditor(), position);
		assertEquals("Name of node should be", "ma:test", sourceNode.getNodeName()); //$NON-NLS-1$ //$NON-NLS-2$
		nsIDOMNode domNode = vpeController.getDomMapping().getNearVisualNode(sourceNode);
		vpeController.getSourceBuilder().openOn(domNode);
		IEditorPart  activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		assertEquals("facelets.taglib.xml file should be opened","facelets.taglib.xml", activeEditor.getEditorInput().getName());  //$NON-NLS-1$//$NON-NLS-2$
		StructuredSelection selection = (StructuredSelection) activeEditor.getEditorSite().getSelectionProvider().getSelection();
		SimpleFileImpl customizedObjectImpl = (SimpleFileImpl) selection.getFirstElement();
		//cheak if selection on right line, how? 
		assertEquals("Children lenght should be","FileSystems/WEB-ROOT/tags/facelets.taglib.xml",customizedObjectImpl.getLongPath()); //$NON-NLS-1$ //$NON-NLS-2$

	}
	/**
	 * 
	 * @throws CoreException
	 */
	public void testOpenOnforFacelets() throws CoreException {
		VpeController vpeController =	openInVpe(JsfAllTests.IMPORT_CUSTOM_FACELETS_PROJECT, "inputName.xhtml"); //$NON-NLS-1$
		int position = TestUtil.getLinePositionOffcet(vpeController.getSourceEditor().getTextViewer(), 10, 38);
		Node sourceNode = SelectionUtil.getNodeBySourcePosition(vpeController.getSourceEditor(), position);
		nsIDOMNode domNode = vpeController.getDomMapping().getNearVisualNode(sourceNode);
		vpeController.getSourceBuilder().openOn(domNode);
		IEditorPart  activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		assertEquals("facelets.taglib.xml file should be opened","common.xhtml", activeEditor.getEditorInput().getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
