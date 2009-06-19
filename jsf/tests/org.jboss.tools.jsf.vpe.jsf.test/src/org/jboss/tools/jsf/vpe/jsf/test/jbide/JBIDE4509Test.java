/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import java.lang.reflect.Method;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author mareshkau
 *
 */
public class JBIDE4509Test extends VpeTest{

	public JBIDE4509Test(String name) {
		super(name);
	}
	//tests openOn from  taglib
	public void testOpenOnTaglibDefinitionFile() throws Throwable {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				JsfAllTests.IMPORT_CUSTOM_FACELETS_PROJECT);
		IFile file = (IFile) project.findMember("WebContent/tags/facelets.taglib.xml"); //$NON-NLS-1$
		IEditorInput input = new FileEditorInput(file);
		MultiPageEditorPart editorPart = (MultiPageEditorPart) PlatformUI.getWorkbench().
								getActiveWorkbenchWindow().
								getActivePage().
								openEditor(input,getEditorId(file.getName()));
		IEditorPart[] editorParts = editorPart.findEditors(input);
		editorPart.setActiveEditor(editorParts[0]);
		StructuredTextEditor textEditor = (StructuredTextEditor) editorParts[0];

		int openOnPosition = TestUtil.getLinePositionOffcet(textEditor.getTextViewer(),12,17);
		//hack to get hyperlinks detectors, no other was have been founeded
		Method method = AbstractTextEditor.class.getDeclaredMethod("getSourceViewerConfiguration"); //$NON-NLS-1$
		method.setAccessible(true);
		SourceViewerConfiguration sourceViewerConfiguration = (SourceViewerConfiguration) method.invoke(textEditor);
		IHyperlinkDetector[] hyperlinkDetectors = sourceViewerConfiguration.getHyperlinkDetectors(textEditor.getTextViewer());
		for (IHyperlinkDetector iHyperlinkDetector : hyperlinkDetectors) {
			IHyperlink [] hyperLinks = iHyperlinkDetector.detectHyperlinks(textEditor.getTextViewer(), new Region(openOnPosition,0), false);
			if(hyperLinks!=null && hyperLinks.length>0 && hyperLinks[0] instanceof AbstractHyperlink) {
				AbstractHyperlink abstractHyperlink = (AbstractHyperlink) hyperLinks[0];
				abstractHyperlink.open();
				break;
			}
		}
		IEditorPart  activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		assertEquals("Active page should be ","paginator.xhtml", activeEditor.getEditorInput().getName());  //$NON-NLS-1$//$NON-NLS-2$
		}
			
	private static String getEditorId(String filename) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry
				.getDefaultEditor(filename);
		if (descriptor != null)
			return descriptor.getId();
		return EditorsUI.DEFAULT_TEXT_EDITOR_ID;
	}
}
