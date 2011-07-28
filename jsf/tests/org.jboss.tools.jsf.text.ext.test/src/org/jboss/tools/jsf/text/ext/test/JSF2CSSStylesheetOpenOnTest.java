/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkDetector;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * 
 * @author jeremy
 *
 */
public class JSF2CSSStylesheetOpenOnTest  extends TestCase {
	private static final String PROJECT_NAME = "JSF2CompositeOpenOn";
	private static final String PAGE_NAME =  PROJECT_NAME+"/WebContent/pages/inputname.xhtml";
	
	public IProject project = null;

	protected void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	protected void tearDown() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}

	public JSF2CSSStylesheetOpenOnTest() {
		super("JSF2 OpenOn on CSS Stylesheets test");
	}

	public void testJSF2CSSStylesheetOpenOn() throws PartInitException, BadLocationException {
		final String editorName = "style.css";
		final String tagName = "outputStylesheet";  
		final String valueToFind = "style.css";  
		IEditorPart editor = WorkbenchUtils.openEditor(PAGE_NAME);
		assertTrue(editor instanceof JSPMultiPageEditor);
		JSPMultiPageEditor jspMultyPageEditor = (JSPMultiPageEditor) editor;
		ISourceViewer viewer = jspMultyPageEditor.getSourceEditor().getTextViewer(); 
			
		IDocument document = viewer.getDocument();
		IRegion reg = new FindReplaceDocumentAdapter(document).find(0,
				tagName, true, true, false, false);
		assertNotNull("Tag:"+tagName+" not found",reg);
		
		reg = new FindReplaceDocumentAdapter(document).find(reg.getOffset(),
				valueToFind, true, true, false, false);
		assertNotNull("Value to find:"+valueToFind+" not found",reg);
		
		IHyperlink[] links = HyperlinkDetector.getInstance().detectHyperlinks(viewer, reg, true); // new Region(reg.getOffset() + reg.getLength(), 0)
		
		assertNotNull("Hyperlinks for value:"+valueToFind+" are not found",links);
		
		assertTrue("Hyperlinks for value:"+valueToFind+" are not found",links.length!=0);
		
		boolean found = false;
		for(IHyperlink link : links){
			assertNotNull(link.toString());
			
			link.open();
			
			IEditorPart resultEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(editorName.equals(resultEditor.getTitle())){
				found = true;
				return;
			}
		}
		assertTrue("OpenOn have not opened "+editorName+" editor",found);
	}

	public void testJSF2CSSClassOpenOn() throws PartInitException, BadLocationException {
		final String editorName = "style.css";
		final String tagName = "div";  
		final String valueToFind = "info";  
		IEditorPart editor = WorkbenchUtils.openEditor(PAGE_NAME);
		assertTrue(editor instanceof JSPMultiPageEditor);
		JSPMultiPageEditor jspMultyPageEditor = (JSPMultiPageEditor) editor;
		ISourceViewer viewer = jspMultyPageEditor.getSourceEditor().getTextViewer(); 
			
		IDocument document = viewer.getDocument();
		IRegion reg = new FindReplaceDocumentAdapter(document).find(0,
				tagName, true, true, false, false);
		assertNotNull("Tag:"+tagName+" not found",reg);
		
		reg = new FindReplaceDocumentAdapter(document).find(reg.getOffset(),
				valueToFind, true, true, false, false);
		assertNotNull("Value to find:"+valueToFind+" not found",reg);
		
		IHyperlink[] links = HyperlinkDetector.getInstance().detectHyperlinks(viewer, reg, true); // new Region(reg.getOffset() + reg.getLength(), 0)
		
		assertNotNull("Hyperlinks for value:"+valueToFind+" are not found",links);
		
		assertTrue("Hyperlinks for value:"+valueToFind+" are not found",links.length!=0);
		
		boolean found = false;
		for(IHyperlink link : links){
			assertNotNull(link.toString());
			
			link.open();
			
			IEditorPart resultEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(editorName.equals(resultEditor.getTitle())){
				found = true;
				return;
			}
		}
		assertTrue("OpenOn have not opened "+editorName+" editor",found);
	}

}