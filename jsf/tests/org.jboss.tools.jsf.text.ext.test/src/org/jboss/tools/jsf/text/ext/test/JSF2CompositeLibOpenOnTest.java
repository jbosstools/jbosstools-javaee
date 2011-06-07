/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.text.ext.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkDetector;
import org.jboss.tools.jsf.text.ext.hyperlink.JsfJSPTagNameHyperlinkDetector;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JSF2CompositeLibOpenOnTest extends TestCase {
	private static final String PROJECT_NAME = "JSF2CompositeOpenOn";
	private static final String PAGE_NAME = PROJECT_NAME+"/WebContent/pages/inputname.xhtml";
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

	public JSF2CompositeLibOpenOnTest() {
		super("JSF2 OpenOn of composite library test");
	}

	public void testAttribute() throws Exception {
		testOpenon("<ez:input", "label", "input.xhtml", "<composite:attribute name=\"label\"/>");
		testOpenon("<ez:input", "value", "input.xhtml", "<composite:attribute name=\"value\" required=\"true\"/>");
		testOpenon("<ez:input", "action", "input.xhtml", "<composite:attribute name=\"action\" required=\"true\" method-signature=\"java.lang.String f()\"/>");
	}

	public void testTag() throws Exception {
		testOpenon("<ez:input", "input", "input.xhtml", null);
	}
	
	private void testOpenon(String text, String subtext, String editorName, String targetSelection) throws PartInitException, BadLocationException {
		IEditorPart editor = WorkbenchUtils.openEditor(PAGE_NAME);
		assertTrue(editor instanceof JSPMultiPageEditor);
		JobUtils.waitForIdle();
		JSPMultiPageEditor jspMultyPageEditor = (JSPMultiPageEditor) editor;
		ISourceViewer viewer = jspMultyPageEditor.getSourceEditor().getTextViewer(); 
			
		IDocument document = viewer.getDocument();
		int i = document.get().indexOf(text);
		assertTrue(i > 0);
		IRegion reg = new FindReplaceDocumentAdapter(document).find(i,
				subtext, true, true, false, false);
		
		assertNotNull("Region for " + subtext +" not found", reg);
		
		IHyperlink[] links = new JsfJSPTagNameHyperlinkDetector().detectHyperlinks(viewer, reg, false);
		//IHyperlink[] links = HyperlinkDetector.getInstance().detectHyperlinks(viewer, reg, true);
		
		assertNotNull("Hyperlinks for :" + subtext + " are not found",links);
		
		assertTrue("Hyperlinks for tag:" + subtext + " are not found",links.length!=0);
		
		boolean found = false;
		for(IHyperlink link : links){
			assertNotNull(link.toString());
			
			link.open();
			JobUtils.waitForIdle(2000);
			
			IEditorPart resultEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(editorName.equals(resultEditor.getTitle())){
				found = true;
				if(targetSelection != null) {
					ISelection selection = resultEditor.getSite().getSelectionProvider().getSelection();
					assertTrue(selection instanceof ITextSelection);
					ITextSelection textSelection = (ITextSelection)selection;
					JSPMultiPageEditor jspMultyPageEditor2 = (JSPMultiPageEditor) resultEditor;
					ISourceViewer viewer2 = jspMultyPageEditor2.getSourceEditor().getTextViewer(); 
					IDocument document2 = viewer2.getDocument();
					String selectedText = document2.get().substring(textSelection.getOffset(), textSelection.getOffset() + textSelection.getLength());
					assertEquals(selectedText, targetSelection);
				}
			}
		}
		if(!found) {
			assertTrue("OpenOn have not opened "+editorName+" editor",found);
		}
	}
	
}

