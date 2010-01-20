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

public class PreferencesForEditors_JBIDE5692 extends VpeTest {

	private JSPMultiPageEditor part1;
	private JSPMultiPageEditor part2;
	private JSPMultiPageEditor part3;
	
	public PreferencesForEditors_JBIDE5692(String name) {
		super(name);
	}
	
	public void testPreferencesForEditors() throws Throwable {
		setException(null);
		openFirstTestPage();
		openSecondTestPage();
		openThirdTestPage();
	}
	
	private void openFirstTestPage() throws Throwable{
		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/5692/test1.jsp", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		IEditorInput input = new FileEditorInput(file);
		assertNotNull("Editor input is null", input); //$NON-NLS-1$
		// open and get editor
		part1 = openEditor(input);
		part1.pageChange(part1.getPreviewIndex());
	}
	
	private void openSecondTestPage() throws Throwable{
		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/5692/test2.jsp", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		IEditorInput input = new FileEditorInput(file);
		assertNotNull("Editor input is null", input); //$NON-NLS-1$
		// open and get editor
		part2 = openEditor(input);
		checkOpenedTab(2,part2.getSelectedPageIndex());
		part2.pageChange(part2.getVisualSourceIndex());
	}
	
	private void openThirdTestPage() throws Throwable{
		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/5692/test3.jsp", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		IEditorInput input = new FileEditorInput(file);
		assertNotNull("Editor input is null", input); //$NON-NLS-1$
		// open and get editor
		part3 = openEditor(input);
		checkOpenedTab(0,part3.getSelectedPageIndex());
	}
	
	private void checkOpenedTab(int expected, int actual){
		assertEquals("Tab index is incorrect ", expected, actual); //$NON-NLS-1$
	}

}
