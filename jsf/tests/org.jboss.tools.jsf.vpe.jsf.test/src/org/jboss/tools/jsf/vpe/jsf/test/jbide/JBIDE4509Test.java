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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
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
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
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
		IEditorInput editorInput = new FileEditorInput(file);
		JBIDE4509Test.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 12, 17, "paginator.xhtml"); //$NON-NLS-1$
	}
	
	
	//test openon for taglib from in file
	public void testOpenOnForTaglibInJarFile() throws Throwable {
		checkOpenOnFromJarFile("WebContent/WEB-INF/lib/mareshkau.jar", //$NON-NLS-1$
				"META-INF/mareshkau.taglib.xml", 12, 25, "components/paginator.xhtml"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testOpenOnForJavaElementFromDeclarationInJar() throws Throwable {
		checkOpenOnFromJarFile("WebContent/WEB-INF/lib/jsf-facelets.jar", "META-INF/jsf-ui.taglib.xml", //$NON-NLS-1$ //$NON-NLS-2$
				25, 33, "UILibrary.class"); //$NON-NLS-1$
	}
	
	//test for <function-class>
	public void testJBIDE4638OpenOnForFunctionClass() throws Throwable{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				JsfAllTests.IMPORT_CUSTOM_FACELETS_PROJECT);
		IFile file = (IFile) project.findMember("WebContent/tags/facelets.taglib.xml"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		JBIDE4509Test.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 31, 37, "FaceletFunctions.java"); //$NON-NLS-1$
	}
	
	//test for <function-class>
	public void testJBIDE4638OpenOnForLibraryClass() throws Throwable{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME);
		IFile file = (IFile) project.findMember("WebContent/WEB-INF/test.taglib.xml"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		JBIDE4509Test.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 6, 25, "CoreLibrary.java"); //$NON-NLS-1$
	}
	//test for <function-class>
	public void testJBIDE4638OpenOnForHandlerClass() throws Throwable{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				JsfAllTests.IMPORT_CUSTOM_FACELETS_PROJECT);
		IFile file = (IFile) project.findMember("WebContent/tags/facelets.taglib.xml"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		JBIDE4509Test.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 22, 23, "IfHandler.java"); //$NON-NLS-1$
	}
	//test for https://jira.jboss.org/jira/browse/JBIDE-4635
	public void testJBIDE4635OpenOn() throws Throwable{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME);
		IFile file = (IFile) project.findMember("WebContent/pages/index.xhtml"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		JBIDE4509Test.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 11, 11, "mareshkau.taglib.xml"); //$NON-NLS-1$
		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		JBIDE4509Test.checkOpenOnInEditor(activeEditor.getEditorInput(),getEditorId(activeEditor.getEditorInput().getName()),
				8,23,"echo.xhtml"); //$NON-NLS-1$
	}
	/**
	 * Function for checking openOn functionality in jar file;
	 * 
	 * @param jarFilePath
	 * @param jarEntryPath
	 * @param line
	 * @param position
	 * @param expectedResult
	 * @throws Throwable
	 * 
	 * @author mareshkau
	 */
	private static final void checkOpenOnFromJarFile(final String jarFilePath,final String jarEntryPath,
			final int line, final int position,final String expectedResult) throws Throwable {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME);
		IJavaProject javaProject = JavaCore.create(project);
		
		IFile jarArchive = (IFile) project.findMember(jarFilePath); //$NON-NLS-1$

		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(jarArchive);
		
		JarPackageFragmentRoot jarRoot = (JarPackageFragmentRoot) root; 
		JarEntryFile fileInJar = new JarEntryFile(jarEntryPath); //$NON-NLS-1$s
		fileInJar.setParent(jarRoot);
		JarEntryEditorInput jarEditorInput = new JarEntryEditorInput(fileInJar);
		JBIDE4509Test.checkOpenOnInEditor(jarEditorInput, getEditorId(fileInJar.getName()),line, position, 
				expectedResult); 
	}
	/**
	 * Function for checking openOn functionality
	 * 
	 * @param editorInput
	 * @param editorId
	 * @param lineNumber
	 * @param lineOffset
	 * @param openedOnFileName
	 * @throws Throwable
	 * 
	 * @author mareshkau
	 */
	private static final void checkOpenOnInEditor(IEditorInput editorInput,String editorId,int lineNumber, int lineOffset, String openedOnFileName) throws Throwable {
		IEditorPart editorPart = PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.openEditor(editorInput, editorId);
		StructuredTextEditor textEditor = null;
		if(editorPart instanceof MultiPageEditorPart){
			IEditorPart[] editorParts = ((MultiPageEditorPart)editorPart).findEditors(editorInput);
			((MultiPageEditorPart)editorPart).setActiveEditor(editorParts[0]);
			textEditor = (StructuredTextEditor) editorParts[0];
		} else if(editorPart instanceof JSPMultiPageEditor) {
			textEditor = ((JSPMultiPageEditor)editorPart).getSourceEditor();
		} else if(editorPart instanceof EditorPartWrapper) {
			IEditorPart[] editorParts = ((MultiPageEditorPart)((EditorPartWrapper)editorPart).getEditor()).findEditors(editorInput);
			((MultiPageEditorPart)((EditorPartWrapper)editorPart).getEditor()).setActiveEditor(editorParts[1]);
			textEditor = (StructuredTextEditor) editorParts[1];
		}
		int openOnPosition = TestUtil.getLinePositionOffcet(textEditor
				.getTextViewer(),lineNumber, lineOffset);
		// hack to get hyperlinks detectors, no other was have been founded
		Method method = AbstractTextEditor.class
				.getDeclaredMethod("getSourceViewerConfiguration"); //$NON-NLS-1$
		method.setAccessible(true);
		SourceViewerConfiguration sourceViewerConfiguration = (SourceViewerConfiguration) method
				.invoke(textEditor);
		IHyperlinkDetector[] hyperlinkDetectors = sourceViewerConfiguration
				.getHyperlinkDetectors(textEditor.getTextViewer());
		for (IHyperlinkDetector iHyperlinkDetector : hyperlinkDetectors) {
			IHyperlink[] hyperLinks = iHyperlinkDetector.detectHyperlinks(
					textEditor.getTextViewer(), new Region(openOnPosition, 0),
					false);
			if (hyperLinks != null && hyperLinks.length > 0
					&& hyperLinks[0] instanceof AbstractHyperlink) {
				AbstractHyperlink abstractHyperlink = (AbstractHyperlink) hyperLinks[0];
				abstractHyperlink.open();
				break;
			}
		}
		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		assertEquals(
				"Active page should be ", openedOnFileName, activeEditor.getEditorInput().getName()); //$NON-NLS-1$

	}
	
	
	private static final String getEditorId(String filename) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry
				.getDefaultEditor(filename);
		if (descriptor != null)
			return descriptor.getId();
		return EditorsUI.DEFAULT_TEXT_EDITOR_ID;
	}
}
