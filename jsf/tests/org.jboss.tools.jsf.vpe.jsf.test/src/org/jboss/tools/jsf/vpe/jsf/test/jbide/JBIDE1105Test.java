/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author mareshkau
 * 
 */
public class JBIDE1105Test extends VpeTest {

	private static final String TEST_PAGE_NAME = "JBIDE/1105/employee.xhtml"; //$NON-NLS-1$

	public JBIDE1105Test(String name) {
		super(name);
	}

	public void testJBIDE1105() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		setException(null);
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME,
				JsfAllTests.IMPORT_PROJECT_NAME);

		assertNotNull("Could not open specified file " + TEST_PAGE_NAME, file); //$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$

		final JSPMultiPageEditor parts = openEditor(input);
		TestUtil.waitForIdle();
		assertNotNull(parts);

		StyledText styledText = parts.getSourceEditor().getTextViewer()
				.getTextWidget();
		String delimiter = styledText.getLineDelimiter();
		for (int i = 0; i < 10; i++) {
			int offset = styledText.getOffsetAtLine(21);
			styledText.setCaretOffset(offset - delimiter.length());
			styledText.insert(delimiter);
			TestUtil.waitForIdle();
		}
		for (int i = 0; i < 10; i++) {
			int offset = styledText.getOffsetAtLine(23);
			styledText.setCaretOffset(offset - " Test ".length() //$NON-NLS-1$
					- delimiter.length());
			styledText.insert(" Test "); //$NON-NLS-1$
			TestUtil.waitForIdle();
		}

		TestUtil.waitForIdle();
		TestUtil.delay(1000L);

		if (getException() != null) {
			throw getException();
		}
	}

}
