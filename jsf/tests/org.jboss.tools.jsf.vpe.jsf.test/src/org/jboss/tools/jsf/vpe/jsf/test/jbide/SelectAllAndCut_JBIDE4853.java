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

import org.eclipse.swt.custom.StyledText;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;

/**
 * Tests for JIRA issue JBIDE-4859: AutoRefresh doesn't work after 
 * Select All and source edit.
 * (https://jira.jboss.org/jira/browse/JBIDE-4859 )
 * 
 * @author yradtsevich
 */
public class SelectAllAndCut_JBIDE4853 extends VpeTest {
	private static final String TEST_PAGE_NAME
			= "JBIDE/4853/SelectAllAndCut.html"; //$NON-NLS-1$
	private static final String CUT_ELEMENT_ID = "cut-div"; //$NON-NLS-1$

	public SelectAllAndCut_JBIDE4853(String name) {
		super(name);
	}

	public void testSelectAllAndCut() throws Throwable {
		VpeController vpeController = openInVpe(JsfAllTests.IMPORT_PROJECT_NAME,
				TEST_PAGE_NAME);
		StyledText textWidget = vpeController.getSourceEditor()
				.getTextViewer().getTextWidget();
		
		textWidget.selectAll();
		textWidget.cut();
		TestUtil.waitForIdle();
		
		nsIDOMDocument document = vpeController.getXulRunnerEditor()
				.getDOMDocument();
		assertNull("Element with id='" + CUT_ELEMENT_ID //$NON-NLS-1$
					+ "' has been cut, but still"       //$NON-NLS-1$
					+ " exists in the visual part.",    //$NON-NLS-1$
				document.getElementById(CUT_ELEMENT_ID));
	}
}
