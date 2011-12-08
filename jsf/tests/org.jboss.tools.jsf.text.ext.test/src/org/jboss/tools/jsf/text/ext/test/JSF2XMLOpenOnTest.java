/*******************************************************************************
 * Copyright (c) 2011 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkDetector;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.jst.text.ext.hyperlink.ELHyperlink;
import org.jboss.tools.jst.text.ext.hyperlink.ELHyperlinkDetector;
import org.jboss.tools.jst.web.ui.editors.TLDCompoundEditor;
import org.jboss.tools.jsf.text.ext.hyperlink.BundleBasenameHyperlink;
import org.jboss.tools.jsf.text.ext.test.JSFHyperlinkTestUtil.TestHyperlink;
import org.jboss.tools.jsf.text.ext.test.JSFHyperlinkTestUtil.TestRegion;
import org.jboss.tools.jsf.ui.editor.FacesConfigEditor;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

public class JSF2XMLOpenOnTest extends TestCase {
	private static final String PROJECT_NAME = "JSF2CompositeOpenOn";
	private static final String PAGE_NAME =  "/WebContent/WEB-INF/faces-config.xml";
	
	public IProject project = null;

	protected void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	protected void tearDown() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}

	public JSF2XMLOpenOnTest() {
		super("JSF2 OpenOn on messages test");
	}
	
	public void testELHyperlink() throws Exception{

		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(992, 5, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open 'Person - demo'", "Person.java")}));
		regionList.add(new TestRegion(999, 3, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open 'Person.getName() - demo'", "Person.java")}));
		
		JSFHyperlinkTestUtil.checkRegions(project, PAGE_NAME, regionList, new ELHyperlinkDetector());
		
	}

	/**
	 * Test opening resource from
  	 * <resource-bundle>
   	 *  <base-name>resources</base-name>
   	 *  <var>registeredMsgs</var>
 	 * </resource-bundle>
	 * @throws Exception
	 */
	public void testResourceBundleHyperlink() throws Exception{
		String editorName = "resources.properties";
		String linkName = "Open bundle for base name 'resources'";
		HashSet<IEditorPart> openedEditors = new HashSet<IEditorPart>();

		IEditorPart editor = WorkbenchUtils.openEditor(PROJECT_NAME + PAGE_NAME);
		if (editor != null) openedEditors.add(editor);
		if(editor instanceof EditorPartWrapper) {
			editor = ((EditorPartWrapper)editor).getEditor();
		}
		assertTrue(editor instanceof FacesConfigEditor);
		try {
			FacesConfigEditor tldEditor = (FacesConfigEditor)editor;
			tldEditor.selectPageByName("Source");
			ISourceViewer viewer = tldEditor.getSourceEditor().getTextViewer();
			IHyperlink[] links = findLinks(viewer, "base-name", "resources");
			IEditorPart resultEditor = findEditor(links, linkName, editorName, openedEditors);
			assertNotNull("OpenOn have not opened "+editorName+" editor", resultEditor);
		} finally {
			closeEditors(openedEditors);
		}
	}

	private IEditorPart findEditor(IHyperlink[] links, String linkName, String editorName, Set<IEditorPart> openedEditors) {
		for(IHyperlink link : links){
			link.getHyperlinkRegion();
			if(!linkName.equals(link.getHyperlinkText())) {
				continue;
			}
			assertNotNull(link.toString());
			link.open();
			IEditorPart resultEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (resultEditor != null) openedEditors.add(resultEditor);
			if(editorName.equals(resultEditor.getTitle())){
				return resultEditor;
			}
		}
		return null;
	}

	private IHyperlink[] findLinks(ISourceViewer viewer, String tagName, String valueToFind) throws BadLocationException {
		IDocument document = viewer.getDocument();
		IRegion reg = new FindReplaceDocumentAdapter(document).find(0,
				tagName, true, true, false, false);
		assertNotNull("Tag:"+tagName+" not found",reg);
		reg = new FindReplaceDocumentAdapter(document).find(reg.getOffset(),
				valueToFind, true, true, false, false);
		assertNotNull("Value to find:"+valueToFind+" not found",reg);
		IHyperlink[] links = HyperlinkDetector.getInstance().detectHyperlinks(viewer, reg, true); // new Region(reg.getOffset() + reg.getLength(), 0)
		assertTrue("Hyperlinks for value '"+valueToFind+"' are not found",(links != null && links.length > 0));
		return links;
	}

	protected void closeEditors (HashSet<IEditorPart> editors) {
		if (editors == null || editors.isEmpty()) 
			return;
		for (IEditorPart editor : editors) {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().closeEditor(editor, false);
		}
	}
	
}

