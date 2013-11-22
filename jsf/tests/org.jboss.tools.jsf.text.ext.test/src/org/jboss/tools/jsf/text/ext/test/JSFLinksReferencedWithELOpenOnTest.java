/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
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
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkDetector;
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * The JUnit test cases for JBIDE-11140 issue 
 * 
 * @author Victor Rubezhny
 */
public class JSFLinksReferencedWithELOpenOnTest extends TestCase {
	private static final String PROJECT_NAME = "jsfHyperlinkTests";
	private static final String PAGE_NAME =  PROJECT_NAME+"/WebContent/JBIDE-11140/jbide11140.xhtml";
	
	private static final String[][] DATA = { /* tag - value - result editor name (null means external editor is to be opened, f.e. for images) */
		{ "graphicImage", "fakerichfaceslogo.jsp", "fakerichfaceslogo.jsp" },
		{ "loadStyle", "theme.css", "theme.css" }
	};
	
	
	public IProject project = null;
	public String naturesCheckProperty;

	protected void setUp() {
		naturesCheckProperty = System.getProperty("org.jboss.tools.vpe.ENABLE_PROJECT_NATURES_CHECKER");  //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("org.jboss.tools.vpe.ENABLE_PROJECT_NATURES_CHECKER", "false");  //$NON-NLS-1$ //$NON-NLS-2$
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	protected void tearDown() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		System.setProperty("org.jboss.tools.vpe.ENABLE_PROJECT_NATURES_CHECKER", naturesCheckProperty == null ? "null" : naturesCheckProperty);  //$NON-NLS-1$ 
	}
	
	public JSFLinksReferencedWithELOpenOnTest() {
		super("JSF OpenOn on links referenced with EL test");
	}

	public void testJSFLinksReferencedWithELOpenOn() throws PartInitException, BadLocationException {
		for (int i = 0; i < DATA.length; i++) {
			final String tagName = DATA[i][0];  
			final String valueToFind = DATA[i][1];
			final String editorName = DATA[i][2];

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
			
			IHyperlink[] links = HyperlinkDetector.getInstance().detectHyperlinks(viewer, reg, true); 
			
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
}
