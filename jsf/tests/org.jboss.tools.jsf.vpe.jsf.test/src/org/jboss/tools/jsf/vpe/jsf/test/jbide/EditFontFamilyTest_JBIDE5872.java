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

import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Test for JBIDE-5872: VPE throws java.lang.NullPointerException,
 * while editing font-family.
 * 
 * @see <a href="https://jira.jboss.org/jira/browse/JBIDE-5872">JBIDE-5872</a>
 * @author Yahor Radtsevich (yradtsevich)
 */
public class EditFontFamilyTest_JBIDE5872 extends VpeTest {
	private static final Point EDIT_POSITION = new Point(3, 40);
	private static final int SELECTION_LENGTH = 1;
	private static final String TEST_PAGE_NAME = "JBIDE/5872/JBIDE-5872.html";

	public EditFontFamilyTest_JBIDE5872(String name) {
		super(name);
	}

	/**
	 * Deletes quote after style attribute. This may lead to NPE.
	 * 
	 * @see <a href="https://jira.jboss.org/jira/browse/JBIDE-5872">JBIDE-5872</a>
	 */
	public void testEditFontFamily() throws Throwable {
		TestUtil.waitForJobs();
		setException(null);

		VpeController vpeController
				= openInVpe(JsfAllTests.IMPORT_PROJECT_NAME, TEST_PAGE_NAME);
		StructuredTextEditor sourceEditor = vpeController.getSourceEditor();
		TextViewer textViewer = sourceEditor.getTextViewer();
		StyledText textWidget = textViewer.getTextWidget();

		int offset = getCaretOffset(textViewer,	EDIT_POSITION);
		textWidget.setSelectionRange(offset, SELECTION_LENGTH);
		textWidget.insert("");
		TestUtil.waitForIdle();
		
		ISelectionProvider selectionProvider = sourceEditor.getSelectionProvider();
		vpeController.selectionChanged(new SelectionChangedEvent(
				selectionProvider, selectionProvider.getSelection()));

		if(getException() != null) {
			throw new Exception(getException());
		}
	}
	
	private static int getCaretOffset(TextViewer textViewer, Point position) {
		return TestUtil.getLinePositionOffcet(textViewer, position.x, position.y);
	}
}
