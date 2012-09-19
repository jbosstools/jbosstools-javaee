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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
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
import org.jboss.tools.jst.text.ext.hyperlink.ELHyperlink;
import org.jboss.tools.jst.text.ext.hyperlink.ELHyperlinkDetector;
import org.jboss.tools.jst.text.ext.hyperlink.ELVarListHyperlink;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * 
 * 
 * @author Viacheslav Kabanovich
 */
public class JSPELHyperlinkUIParamTest  extends TestCase {
	private static final String PROJECT_NAME = "JSF2CompositeOpenOn";
	public IProject project = null;
	private ELHyperlinkDetector elHyperlinkDetector = new ELHyperlinkDetector();

	protected void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	protected void tearDown() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}

	public JSPELHyperlinkUIParamTest() {
		super("JSP UI Param OpenOn test");
	}

	
	public void testELHyperlinkUIParam() throws PartInitException, BadLocationException {
		//Activate page, include model is lazy.
		PageContextFactory.createPageContext(project.getFile(new Path("/WebContent/params/a.xhtml")));
		PageContextFactory.createPageContext(project.getFile(new Path("/WebContent/params/c.xhtml")));
		String pageName = PROJECT_NAME+"/WebContent/params/b.xhtml";
		String textToFind = "myparam1";
		String[] resultEditor = new String[]{"a.xhtml", "Person.java"};
		try {
			doELHyperlinkUIParam(pageName, textToFind, false, resultEditor);
		} finally {
			WorkbenchUtils.closeAllEditors();
		}
		textToFind = "myparam2";
		resultEditor = new String[]{"a.xhtml", "String."};
		try {
			doELHyperlinkUIParam(pageName, textToFind, false, resultEditor);
		} finally {
			WorkbenchUtils.closeAllEditors();
		}
		textToFind = "myparam3";
		resultEditor = new String[]{"a.xhtml", "c.xhtml", "Person.java"};
		try {
			doELHyperlinkUIParam(pageName, textToFind, true, resultEditor);
		} finally {
			WorkbenchUtils.closeAllEditors();
		}
	}
	
	private void doELHyperlinkUIParam(String pageName, String template, boolean hasList, String... editorName) throws BadLocationException {
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
		
		if(hasList) {
			boolean found = false;
			List<IHyperlink> list = new ArrayList<IHyperlink>();		
			for(int i = 0; i < links.length; i++){
				if(links[i] instanceof ELVarListHyperlink) {
					found = true;
					ELHyperlink[] ls = ((ELVarListHyperlink)links[i]).getHyperlinks();
					for (ELHyperlink l: ls) list.add(l);
				} else {
					list.add(links[i]);
				}
			}
			assertTrue(found);
			links = list.toArray(new IHyperlink[0]);
		}
		
		for(int i = 0; i < links.length; i++){
			IHyperlink link = links[i];
			assertNotNull(link.toString());
			
			link.open();
			
			IEditorPart resultEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			assertTrue("Link " + i + " leads to a wrong editor: " + resultEditor.getTitle(), resultEditor.getTitle().startsWith(editorName[i]));
		}
	}
}