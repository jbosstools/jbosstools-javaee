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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.ui.test.OpenOnUtil;
import org.jboss.tools.vpe.ui.test.ProjectsLoader;
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
		IProject project = ProjectsLoader.getInstance()
				.getProject(JsfAllTests.IMPORT_CUSTOM_FACELETS_PROJECT);
		IFile file = (IFile) project.findMember("WebContent/tags/facelets.taglib.xml"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		OpenOnUtil.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 12, 17, "paginator.xhtml"); //$NON-NLS-1$
	}
	
	
	//test openon for taglib from in file
	public void testOpenOnForTaglibInJarFile() throws Throwable {
		checkOpenOnFromJarFile("WebContent/WEB-INF/lib/mareshkau.jar", //$NON-NLS-1$
				"META-INF/mareshkau.taglib.xml", 12, 25, "paginator.xhtml"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testOpenOnForJavaElementFromDeclarationInJar() throws Throwable {
		checkOpenOnFromJarFile("WebContent/WEB-INF/lib/jsf-facelets.jar", "META-INF/jsf-ui.taglib.xml", //$NON-NLS-1$ //$NON-NLS-2$
				25, 33, "UILibrary.class"); //$NON-NLS-1$
	}
	
	//test for <function-class>
	public void testJBIDE4638OpenOnForFunctionClass() throws Throwable{
		IProject project = ProjectsLoader.getInstance()
				.getProject(JsfAllTests.IMPORT_CUSTOM_FACELETS_PROJECT);
		IFile file = (IFile) project.findMember("WebContent/tags/facelets.taglib.xml"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		OpenOnUtil.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 31, 37, "FaceletFunctions.java"); //$NON-NLS-1$
	}
	
	//test for <function-class>
	public void testJBIDE4638OpenOnForLibraryClass() throws Throwable{
		IProject project = ProjectsLoader.getInstance()
				.getProject(JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME);
		IFile file = (IFile) project.findMember("WebContent/WEB-INF/test.taglib.xml"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		OpenOnUtil.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 6, 25, "CoreLibrary.java"); //$NON-NLS-1$
	}
	//test for <function-class>
	public void testJBIDE4638OpenOnForHandlerClass() throws Throwable{
		IProject project = ProjectsLoader.getInstance()
				.getProject(JsfAllTests.IMPORT_CUSTOM_FACELETS_PROJECT);
		IFile file = (IFile) project.findMember("WebContent/tags/facelets.taglib.xml"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		OpenOnUtil.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 22, 23, "IfHandler.java"); //$NON-NLS-1$
	}
	//test for <handler-class> in tag
	public void testJBIDE4638OpenOnForHandlerClassWithTagAxis() throws Throwable{
		IProject project = ProjectsLoader.getInstance()
				.getProject(JsfAllTests.IMPORT_CUSTOM_FACELETS_PROJECT);
		IFile file = (IFile) project.findMember("WebContent/tags/facelets.taglib.xml"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		OpenOnUtil.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 36, 29, "IfHandler.java"); //$NON-NLS-1$
	}
	//test for https://jira.jboss.org/jira/browse/JBIDE-4635
	public void testJBIDE4635OpenOn() throws Throwable{
		IProject project = ProjectsLoader.getInstance()
				.getProject(JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME);
		IFile file = (IFile) project.findMember("WebContent/pages/index.xhtml"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		OpenOnUtil.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 11, 11, "mareshkau.taglib.xml"); //$NON-NLS-1$
		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		OpenOnUtil.checkOpenOnInEditor(activeEditor.getEditorInput(),getEditorId(activeEditor.getEditorInput().getName()),
				8,23,"echo.xhtml"); //$NON-NLS-1$
	}

	//test for https://jira.jboss.org/jira/browse/JBIDE-5099
	public void testJBIDE5099OpenOn() throws Throwable{
		IProject project = ProjectsLoader.getInstance()
				.getProject(JsfAllTests.IMPORT_JSF_20_PROJECT_NAME);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		IFile file = (IFile) project.findMember("WebContent/pages/JBIDE/5015/login.xhtml"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		OpenOnUtil.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 15, 17, "loginPanel.xhtml"); //$NON-NLS-1$
	}

	//test for https://jira.jboss.org/jira/browse/JBIDE-5099
	public void testJBIDE5099JarOpenOn() throws Throwable{
		IProject project = ProjectsLoader.getInstance()
				.getProject(JsfAllTests.IMPORT_JSF_20_PROJECT_NAME);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		IFile file = (IFile) project.findMember("WebContent/pages/JBIDE/5015/login.xhtml"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		OpenOnUtil.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 14, 16, "echo.xhtml"); //$NON-NLS-1$
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
	@SuppressWarnings("restriction")
	private static final void checkOpenOnFromJarFile(final String jarFilePath,final String jarEntryPath,
			final int line, final int position,final String expectedResult) throws Throwable {
		IProject project = ProjectsLoader.getInstance()
				.getProject(JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME);
		IJavaProject javaProject = JavaCore.create(project);
		
		IFile jarArchive = (IFile) project.findMember(jarFilePath);

		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(jarArchive);
		
		JarPackageFragmentRoot jarRoot = (JarPackageFragmentRoot) root; 
		JarEntryFile fileInJar = new JarEntryFile(jarEntryPath);
		fileInJar.setParent(jarRoot);
		JarEntryEditorInput jarEditorInput = new JarEntryEditorInput(fileInJar);
		OpenOnUtil.checkOpenOnInEditor(jarEditorInput, getEditorId(fileInJar.getName()),line, position, 
				expectedResult); 
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
