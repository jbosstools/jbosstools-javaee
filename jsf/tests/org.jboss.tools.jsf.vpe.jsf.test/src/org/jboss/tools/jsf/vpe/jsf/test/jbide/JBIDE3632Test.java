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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;

/**
 * @author mareshkau
 *
 */
public class JBIDE3632Test extends VpeTest {

	public JBIDE3632Test(String name) {
		super(name);
	}
	
	public void testJBIDE3632Test() throws Throwable {
		setException(null);
        IFile ifile = (IFile) TestUtil.getComponentPath("JBIDE/3632/home.xhtml", //$NON-NLS-1$
        		JsfAllTests.IMPORT_PROJECT_NAME);
        IEditorInput input = new FileEditorInput(ifile);
        JSPMultiPageEditor part = openEditor(input);
        //wait for initialization of editor
        TestUtil.getVpeController(part);
        int position = TestUtil.getLinePositionOffcet(part.getSourceEditor().getTextViewer(), 19, 26);
        // sets selection for h:outputText
        part.getSourceEditor().getTextViewer().getTextWidget().setCaretOffset(position);
        // delete one of "
        part.getSourceEditor().getTextViewer().getTextWidget().replaceTextRange(position, 1, ""); //$NON-NLS-1$
        //wait while update job will be running
        TestUtil.delay();
        closeEditors();
		if(getException()!=null) {
			throw getException();
		}
	}
 
}
