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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentType;

public class JBIDE4510Test extends VpeTest {

	private static final String JAR_NAME = "WebContent/WEB-INF/lib/mareshkau.jar"; //$NON-NLS-1$
	private static final String FILE_NAME = "components/paginator.xhtml"; //$NON-NLS-1$
	private static final String PACKAGE_NAME = "components"; //$NON-NLS-1$
	private static final String SHORT_NAME = "paginator.xhtml"; //$NON-NLS-1$
	
	public JBIDE4510Test(String name) {
		super(name);
	}

	public void testCorrectDoctypeOnFileFromJarArchive() throws Throwable {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME);
		/*
		 * Project should exist in the workspace 
		 */
		assertNotNull("Project was not found in the workspace: " //$NON-NLS-1$
				+ JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME, project);
		IFile jarArchive = (IFile) project.findMember(JAR_NAME);
		/*
		 * Jar file should exist in the project. 
		 */
		assertNotNull("File was not found in the project: " //$NON-NLS-1$
				+ JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME + "/" + JAR_NAME, //$NON-NLS-1$
				jarArchive);
		IJavaProject javaProject = JavaCore.create(project);
		/*
		 * Project should be correctly transformed. 
		 */
		assertNotNull("Cannot process java project:" //$NON-NLS-1$
				+ JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME, javaProject);
		
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(jarArchive.getLocation().toString());
		/*
		 * Root element should have the correct type.
		 */
		if (root instanceof JarPackageFragmentRoot) {
			JarPackageFragmentRoot jarRoot = (JarPackageFragmentRoot) root;
			IPackageFragment pf = jarRoot.getPackageFragment(PACKAGE_NAME);
			JarEntryFile jarFile = new JarEntryFile(SHORT_NAME);
			jarFile.setParent(pf);
			JarEntryEditorInput jarEditorInput = new JarEntryEditorInput(jarFile);
			JSPMultiPageEditor editor = openEditor(jarEditorInput);
			/*
			 * Verify that the editor is opened.
			 */
			assertNotNull(
					"Visual Page Editor with a file from the jar archive should have been opened but it wasn't.", //$NON-NLS-1$
					editor);
			/*
			 * Get the DOM document
			 */
			nsIDOMDocument document = TestUtil.getVpeVisualDocument(editor);
			nsIDOMDocumentType doctype = document.getDoctype();
			/*
			 * Doctype should present for the current file.
			 */
			assertNotNull("Doctype should present for the specified file: " //$NON-NLS-1$
					+ JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME + "/" //$NON-NLS-1$
					+ JAR_NAME + "/" + FILE_NAME, doctype); //$NON-NLS-1$
			
			/*
			 * Doctype should have the correct type.
			 */
			assertEquals(
					"Doctype should have the correct type: \" HTML \", but was: "+ doctype.getNodeName(),  //$NON-NLS-1$
					"HTML", doctype.getNodeName()); //$NON-NLS-1$
		} else {
			/*
			 * Fail the test when we cannot process jar file correctly.
			 */
			fail("Jar file cannot be processed. Jar file: " //$NON-NLS-1$
					+ JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME + "/" //$NON-NLS-1$
					+ JAR_NAME);
		}
	}
	
}
