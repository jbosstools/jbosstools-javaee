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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * 
 * @author yzhishko
 *
 */

public class RefreshBundles_JBIDE5460 extends VpeTest {

	private static String TEST_PAGE = "tableBasic/tableBasic.xhtml"; //$NON-NLS-1$
	
	public RefreshBundles_JBIDE5460(String name) {
		super(name);
	}

	public void testRefreshBundles() throws Throwable{
		IFile file = (IFile) getFile(TEST_PAGE, JsfAllTests.IMPORT_JBIDE5460_PROJECT_NAME);

		assertNotNull("Could not open specified file. componentPage = " //$NON-NLS-1$
						+ TEST_PAGE
						+ ";projectName = " + JsfAllTests.IMPORT_JBIDE5460_PROJECT_NAME, file);//$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$
		// open and get editor

		JSPMultiPageEditor part = openEditor(input);

		assertNotNull("Editor is not opened", part); //$NON-NLS-1$

		TestUtil.delay(2000);
	}
	
	private IResource getFile(String pagePath, String projectName) throws CoreException{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				projectName);
		if (project != null) {
			return project.getFolder("WebContent/html").findMember(pagePath);

		}
		return null;
	}
	
}
