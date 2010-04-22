/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
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
import org.eclipse.swt.custom.StyledText;
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

public class ExceptionInVPEComments_JBIDE5143 extends VpeTest {

	public ExceptionInVPEComments_JBIDE5143(String name) {
		super(name);
	}

	public void testExceptionInVPEComments() throws Throwable {
		setException(null);
		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/5143/test.html", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		IEditorInput editorInput = new FileEditorInput(file);
		JSPMultiPageEditor part = openEditor(editorInput);
		TestUtil.delay(3000);
		TestUtil.waitForIdle();
		StyledText styledText = part.getSourceEditor().getTextViewer()
				.getTextWidget();
		String delimiter = styledText.getLineDelimiter();
		int offset = styledText.getOffsetAtLine(8);
		styledText.setCaretOffset(offset - delimiter.length() - 28);
		styledText.insert("-"); //$NON-NLS-1$
		TestUtil.delay(1000);
		TestUtil.waitForIdle();
		styledText.insert("-"); //$NON-NLS-1$
		TestUtil.delay(1000);
		TestUtil.waitForIdle();
		styledText.insert("!"); //$NON-NLS-1$
		TestUtil.delay(1000);
		TestUtil.waitForIdle();
		styledText.insert("<"); //$NON-NLS-1$
		TestUtil.delay(1000);
		TestUtil.waitForIdle();
	}

}
