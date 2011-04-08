/*******************************************************************************
 * Copyright (c) 2010 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
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
import org.jboss.tools.jsf.text.ext.hyperlink.JsfJSPTagNameHyperlinkDetector;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

public class JSF2CompositeOpenOnTest extends TestCase {
	private static final String PROJECT_NAME = "JSF2CompositeOpenOn";
	private static final String PAGE_NAME = PROJECT_NAME+"/WebContent/resources/demo/input.xhtml";
	public IProject project = null;

	protected void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		JobUtils.waitForIdle();
	}
	
	protected void tearDown() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}

	public JSF2CompositeOpenOnTest() {
		super("JSF2 OpenOn on composite test");
	}
	
	private void testTag(String tagName, String editorName) throws PartInitException, BadLocationException {
		IEditorPart editor = WorkbenchUtils.openEditor(PAGE_NAME);
		assertTrue(editor instanceof JSPMultiPageEditor);
		JobUtils.waitForIdle();
		JSPMultiPageEditor jspMultyPageEditor = (JSPMultiPageEditor) editor;
		ISourceViewer viewer = jspMultyPageEditor.getSourceEditor().getTextViewer(); 
			
		IDocument document = viewer.getDocument();
		IRegion reg = new FindReplaceDocumentAdapter(document).find(0,
				tagName, true, true, false, false);
		
		assertNotNull("Tag:"+tagName+" not found",reg);
		
		IHyperlink[] links = new JsfJSPTagNameHyperlinkDetector().detectHyperlinks(viewer, reg, false);
		//IHyperlink[] links = HyperlinkDetector.getInstance().detectHyperlinks(viewer, reg, true);
		
		assertNotNull("Hyperlinks for tag:"+tagName+" are not found",links);
		
		assertTrue("Hyperlinks for tag:"+tagName+" are not found",links.length!=0);
		
		boolean found = false;
		for(IHyperlink link : links){
			assertNotNull(link.toString());
			
			link.open();
			JobUtils.waitForIdle(2000);
			
			IEditorPart resultEdotor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(editorName.equals(resultEdotor.getTitle())){
				found = true;
				return;
			}
		}
		assertTrue("OpenOn have not opened "+editorName+" editor",found);
	}
	
	public void testFormOpenOn() throws PartInitException, BadLocationException {
		testTag("form", "html_basic.tld");
	}

	public void testOutputTextOpenOn() throws PartInitException, BadLocationException {
		testTag("outputText", "html_basic.tld");	
	}

	public void testInputTextTextOpenOn() throws PartInitException, BadLocationException {
		testTag("inputText", "html_basic.tld");	
	}

	public void testCommandButtonOpenOn() throws PartInitException, BadLocationException {
		testTag("commandButton", "html_basic.tld");
	}

}

