/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
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
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JarEntryDirectory;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.jboss.tools.common.model.ui.editor.ModelObjectStorageEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.ui.test.OpenOnUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * 
 * @author yzhishko
 *
 */

@SuppressWarnings("restriction")
public class OpenOnTLDPackedInJar_JBIDE5693 extends VpeTest {

	private static final String JAR_LIB_PATH = "WebContent/WEB-INF/lib/jsf-impl-2.0.1-SNAPSHOT.jar"; //$NON-NLS-1$
	private static final String TEST_FILE = "jsf_core.tld"; //$NON-NLS-1$
	private static final String DIR = "META-INF"; //$NON-NLS-1$
	
	public OpenOnTLDPackedInJar_JBIDE5693(String name) {
		super(name);
	}

	public void testOpenOnTLDPackedInJar() throws Throwable{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				JsfAllTests.IMPORT_JSF_20_PROJECT_NAME);
		IJavaProject javaProject = JavaCore.create(project);
		IPackageFragmentRoot fragmentRoot = javaProject
		.getPackageFragmentRoot(""+project.getLocation() + "/" //$NON-NLS-1$ //$NON-NLS-2$
				+ JAR_LIB_PATH);
		JarPackageFragmentRoot jarPackageFragmentRoot = (JarPackageFragmentRoot) fragmentRoot;
		JarEntryDirectory entryDirectory = new JarEntryDirectory(DIR);
		entryDirectory.setParent(jarPackageFragmentRoot);
		JarEntryFile fileInJar = new JarEntryFile(TEST_FILE);
		fileInJar.setParent(entryDirectory);
		JarEntryEditorInput editorInput = new JarEntryEditorInput(fileInJar);
		OpenOnUtil.checkOpenOnInEditor(editorInput, getEditorId(fileInJar
				.getName()), 71, 15, "CoreValidator.class"); //$NON-NLS-1$

	}
	
	private final String getEditorId(String filename) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry
				.getDefaultEditor(filename);
		if (descriptor != null)
			return descriptor.getId();
		return EditorsUI.DEFAULT_TEXT_EDITOR_ID;
	}

}
