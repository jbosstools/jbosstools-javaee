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
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.jst.text.ext.hyperlink.ELHyperlinkDetector;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * The JUnit test cases for JBIDE-5385, JBIDE-9930 issues 
 * 
 * @author Victor Rubezhny
 */
public class JSPELHyperlinkTestForELInTagBodyTest  extends TestCase {
	private static final String PROJECT_NAME = "jsfHyperlinkTests";
	private static final String[] PAGE_NAMES =  new String[] {
		PROJECT_NAME+"/WebContent/JBIDE-9930/elInTagBody.jsp", 
		PROJECT_NAME+"/WebContent/JBIDE-9930/anotherELInTagBody.jsp", 
	};
	private static final String[][] TEXT_TO_FIND = new String [][] {
		{"bean1", "bean1.property1", "bean1['property1"},
		{"msgs", "msgs.greeting"}
	};
	private static final String[][] RESULT_EDITORS = new String [][] {
		{"Bean1.java", "Bean1.java", "Bean1.java"},
		{"resources.properties", "resources.properties"}
	};
	
	public IProject project = null;
	public String naturesCheckProperty;
	private ELHyperlinkDetector elHyperlinkDetector = new ELHyperlinkDetector();
	 
	
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
	
	public JSPELHyperlinkTestForELInTagBodyTest() {
		super("JSP EL In Tag Body OpenOn test");
	}

	
	public void testJSPELHyperlinkTestForELInTagBody() throws PartInitException, BadLocationException {
		try {
			for (int i = 0; i < PAGE_NAMES.length; i++) {
				for (int j = 0; j < TEXT_TO_FIND[i].length; j++) {
					doJSPELHyperlinkTestForELInTagBodyTest(PAGE_NAMES[i], TEXT_TO_FIND[i][j], RESULT_EDITORS[i][j]);
				}
			}
		} finally {
			WorkbenchUtils.closeAllEditors();
		}
	}
	
	private void doJSPELHyperlinkTestForELInTagBodyTest(String pageName, String template, String editorName) throws BadLocationException {
		IEditorPart editor = WorkbenchUtils.openEditor(pageName);
		assertTrue(editor instanceof JSPMultiPageEditor);
		JSPMultiPageEditor jspMultyPageEditor = (JSPMultiPageEditor) editor;
		ISourceViewer viewer = jspMultyPageEditor.getSourceEditor().getTextViewer(); 
		assertNotNull("Viewer couldn't be found for " + pageName, viewer);
		IDocument document = viewer.getDocument();
		IRegion reg = new FindReplaceDocumentAdapter(document).find(0,
				template, true, true, false, false);
		assertNotNull("Text: "+ template +" not found",reg);
		
		IHyperlink[] links = elHyperlinkDetector.detectHyperlinks(viewer, new Region(reg.getOffset() + reg.getLength() - 1, 0), true); 
		
		assertNotNull("Hyperlinks for EL:#{" + template + "} are not found",links);
		
		assertTrue("Hyperlinks for EL: #{" + template + "} are not found",links.length!=0);
		
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