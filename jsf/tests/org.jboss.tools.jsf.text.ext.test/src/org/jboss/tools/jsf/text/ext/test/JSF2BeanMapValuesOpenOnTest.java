/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
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
 * JUnit test for JBIDE-12609
 * 
 * @author Victor V. Rubezhny
 *
 */
public class JSF2BeanMapValuesOpenOnTest extends TestCase {
	private static final String PROJECT_NAME = "JSF2CompositeOpenOn";
	private static final String PAGE_NAME = PROJECT_NAME+"/WebContent/templates/common.xhtml";
	private static final String SOURCE_PAGE_NAME = PROJECT_NAME+"/WebContent/pages/inputname3.xhtml";
	private static final String EL0 = "#{user}";
	private static final String EL0_SEGMENT = "user";
	private static final String EL = "#{group.users['admin'].name}";
	private static final String EL_SEGMENT0 = "admin";
	private static final String EL_SEGMENT1 = "name";
	private static final String EDITOR_NAME = "User.java";
	private ELHyperlinkDetector elHyperlinkDetector = new ELHyperlinkDetector();
	
	public IProject project = null;

	protected void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	protected void tearDown() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}

	public JSF2BeanMapValuesOpenOnTest() {
		super("JSF2 OpenOn on Bean Map Values test");
	}

	public void testBeanMapValues() throws PartInitException, BadLocationException {
		IEditorPart editor = WorkbenchUtils.openEditor(SOURCE_PAGE_NAME);
		assertTrue(editor instanceof JSPMultiPageEditor);
		JSPMultiPageEditor jspMultyPageEditor = (JSPMultiPageEditor) editor;
		ISourceViewer viewer = jspMultyPageEditor.getSourceEditor().getTextViewer(); 

		IDocument document = viewer.getDocument();
		IRegion reg = new FindReplaceDocumentAdapter(document).find(0,
				EL0, true, true, false, false);
		
		assertNotNull("EL: " + EL0 + " not found",reg);
		reg = new FindReplaceDocumentAdapter(document).find(reg.getOffset(),
				EL0_SEGMENT, true, true, false, false);
		
		assertNotNull("Segment: " + EL0_SEGMENT + " not found in EL",reg);
		
		IHyperlink[] links = elHyperlinkDetector.detectHyperlinks(viewer, new Region(reg.getOffset() + reg.getLength() - 1, 0), false);
		
		assertNotNull("Hyperlinks for EL Segment:"+EL0_SEGMENT+" not found",links);
		
		assertTrue("Hyperlinks for EL Segment:"+EL0_SEGMENT+" not found",links.length!=0);
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);

		editor = WorkbenchUtils.openEditor(PAGE_NAME);
		assertTrue(editor instanceof JSPMultiPageEditor);
		jspMultyPageEditor = (JSPMultiPageEditor) editor;
		viewer = jspMultyPageEditor.getSourceEditor().getTextViewer(); 

		document = viewer.getDocument();
		IRegion reg0 = new FindReplaceDocumentAdapter(document).find(0,
				EL, true, true, false, false);
		
		assertNotNull("EL: " + EL + " not found",reg0);

		// Open On over 'admin' argument shouldn't return any links
		try {
			reg = new FindReplaceDocumentAdapter(document).find(reg0.getOffset(),
					EL_SEGMENT0, true, true, false, false);
			
			assertNotNull("Segment: " + EL_SEGMENT0 + " not found in EL",reg);
			
			links = elHyperlinkDetector.detectHyperlinks(viewer, new Region(reg.getOffset() + reg.getLength() - 1, 0), false);
			
			assertTrue("Some Hyperlinks found for EL Segment:"+EL_SEGMENT0+"!",(links == null || links.length == 0));
		} catch (NullPointerException e) {
			fail("NPE occurred while trying to get Open On over the Segment:" + EL_SEGMENT0);
		}
		
		// Open On over 'name' segment should return a link to User.java
		reg = new FindReplaceDocumentAdapter(document).find(reg0.getOffset(),
				EL_SEGMENT1, true, true, false, false);
		
		assertNotNull("Segment: " + EL_SEGMENT1 + " not found in EL",reg);
		
		links = elHyperlinkDetector.detectHyperlinks(viewer, new Region(reg.getOffset() + reg.getLength() - 1, 0), false);
		
		assertNotNull("Hyperlinks for EL Segment:"+EL_SEGMENT1+" not found",links);
		
		assertTrue("Hyperlinks for EL Segment:"+EL_SEGMENT1+" not found",links.length!=0);
		
		boolean found = false;
		for(IHyperlink link : links){
			assertNotNull(link.toString());
			
			link.open();
			
			IEditorPart resultEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(EDITOR_NAME.equals(resultEditor.getTitle())){
				found = true;
				return;
			}
		}
		assertTrue("OpenOn have not opened "+EDITOR_NAME+" editor",found);
		
	}
}
