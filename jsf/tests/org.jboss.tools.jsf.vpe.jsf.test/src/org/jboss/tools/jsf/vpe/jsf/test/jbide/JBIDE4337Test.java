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
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;

/**
 * @author mareshkau
 *
 */
public class JBIDE4337Test extends VpeTest {

	public JBIDE4337Test(String name) {
		super(name);
	}

	public void testJBIDE4337() throws Exception  {
		
		final String testFileName = "JBIDE/4337/jbide4337.xhtml";//$NON-NLS-1$
		setException(null);

		VpeController vpeController = openInVpe(JsfAllTests.IMPORT_PROJECT_NAME,testFileName);
		long headChildCount = vpeController.getVisualBuilder().getHeadNode().getChildNodes().getLength();
		
		StyledText styledText = vpeController.getSourceEditor().getTextViewer().getTextWidget();
		int offset = TestUtil.getLinePositionOffcet(vpeController.getSourceEditor().getTextViewer(), 11, 23);
		styledText.setCaretOffset(offset);
		styledText.insert(" "); //$NON-NLS-1$
		TestUtil.delay();
		TestUtil.waitForJobs();
		assertEquals("Number of child nodes should be equal before and after change",headChildCount, //$NON-NLS-1$
				vpeController.getVisualBuilder().getHeadNode().getChildNodes().getLength());
		
	}
}
