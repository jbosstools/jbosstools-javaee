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
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.ui.test.OpenOnUtil;
import org.jboss.tools.vpe.ui.test.ProjectsLoader;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author V.I.Kabanovich
 *
 */
public class JBIDE5920Test extends VpeTest{

	public JBIDE5920Test(String name) {
		super(name);
	}

	//test for https://jira.jboss.org/jira/browse/JBIDE-5920
	public void testOpenOnInProjectWith2URLPatterns() throws Throwable{
		IProject project = ProjectsLoader.getInstance()
				.getProject(JsfAllTests.IMPORT_TEST_WITH_2_URL_PATTERNS_PROJECT_NAME);
		IFile file = (IFile) project.findMember("WebContent/index.jsp"); //$NON-NLS-1$
		IEditorInput editorInput = new FileEditorInput(file);
		OpenOnUtil.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 5, 30, "welcome.xhtml"); //$NON-NLS-1$
		
		file = (IFile) project.findMember("WebContent/welcome.xhtml"); //$NON-NLS-1$
		editorInput = new FileEditorInput(file);
		OpenOnUtil.checkOpenOnInEditor(editorInput, getEditorId(file.getName()), 7, 43, "main.xhtml"); //$NON-NLS-1$
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
