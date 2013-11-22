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
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;

/**
 * 
 * @author yzhishko
 *
 */

public class RefreshBundles_JBIDE5460 extends VpeTest {

	private static String TEST_PAGE = "html/tableBasic/tableBasic.xhtml"; //$NON-NLS-1$
	
	public RefreshBundles_JBIDE5460(String name) {
		super(name);
	}

	public void testRefreshBundles() throws Throwable{
		IFile file = (IFile) TestUtil.getWebContentPath(
				TEST_PAGE, JsfAllTests.IMPORT_JBIDE5460_PROJECT_NAME);

		assertNotNull("Could not open specified file. componentPage = " //$NON-NLS-1$
						+ TEST_PAGE
						+ ";projectName = " + JsfAllTests.IMPORT_JBIDE5460_PROJECT_NAME, file);//$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$
		// open and get editor

		JSPMultiPageEditor part = openEditor(input);

		assertNotNull("Editor is not opened", part); //$NON-NLS-1$

		TestUtil.delay();
	}
}
