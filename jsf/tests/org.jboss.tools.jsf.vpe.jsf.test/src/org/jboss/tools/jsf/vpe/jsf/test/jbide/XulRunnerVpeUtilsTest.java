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

import java.text.MessageFormat;

import org.eclipse.swt.graphics.Rectangle;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.jboss.tools.vpe.xulrunner.util.XulRunnerVpeUtils;
import org.mozilla.interfaces.nsIDOMElement;

/**
 * Tests for {@link XulRunnerVpeUtils} class. 
 * 
 * @author Yahor Radtsevich (yradtsevich)
 * @see <a href="https://jira.jboss.org/browse/JBIDE-7153">JBIDE-7153</a>
 */
public class XulRunnerVpeUtilsTest extends VpeTest {
	private static final String THE_Y_POSITION_IS_WRONG = "The y position of {0} is wrong"; //$NON-NLS-1$
	private static final String THE_X_POSITION_IS_WRONG = "The x position of {0} is wrong"; //$NON-NLS-1$
	private static final String THE_HEIGHT_IS_WRONG = "The height of {0} is wrong"; //$NON-NLS-1$
	private static final String THE_WIDTH_IS_WRONG = "The width of {0} is wrong"; //$NON-NLS-1$
	private static final String SPAN1_ID = "span1"; //$NON-NLS-1$
	private static final String INNER_DIV_ID = "innerDiv"; //$NON-NLS-1$
	private static final String OUTER_DIV_ID = "outerDiv"; //$NON-NLS-1$
	private static final int DIV_MARGIN = 13;
	private static final int DIV_PADDING = 7;
	private static final int DIV_BORDER_WIDTH = 11;
	private static final int OUTER_DIV_HEIGHT = 2000;
	private static final int OUTER_DIV_WIDTH = 3000;
	private static final String TEST_FILE_PATH
			= "JBIDE/7153/getElementBounds.html"; //$NON-NLS-1$
	private VpeController vpeController;

	public XulRunnerVpeUtilsTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		vpeController = openInVpe(JsfAllTests.IMPORT_PROJECT_NAME,
				TEST_FILE_PATH);
	}
	
	////////////////////////////////////////////////////////////////////////////
	// JUNIT TESTING METHODS
	public void testGetElementBounds() {
		 Rectangle outerDivSize = XulRunnerVpeUtils.getElementBounds(
				 getElementById(OUTER_DIV_ID));
		 assertEquals(MessageFormat.format(THE_WIDTH_IS_WRONG, OUTER_DIV_ID),
				 OUTER_DIV_WIDTH + DIV_BORDER_WIDTH * 2 + DIV_PADDING * 2,
				 outerDivSize.width);
		 assertEquals(MessageFormat.format(THE_HEIGHT_IS_WRONG, OUTER_DIV_ID),
				 OUTER_DIV_HEIGHT + DIV_BORDER_WIDTH * 2 + DIV_PADDING * 2,
				 outerDivSize.height);
		 
		 Rectangle innerDivSize = XulRunnerVpeUtils.getElementBounds(
				 getElementById(INNER_DIV_ID));
		 assertEquals(MessageFormat.format(THE_WIDTH_IS_WRONG, INNER_DIV_ID),
				 150 + DIV_BORDER_WIDTH * 2 + DIV_PADDING * 2,
				 innerDivSize.width);
		 assertEquals(MessageFormat.format(THE_HEIGHT_IS_WRONG, INNER_DIV_ID),
				 100 + DIV_BORDER_WIDTH * 2 + DIV_PADDING * 2,
				 innerDivSize.height);
		 
		 Rectangle span1Size = XulRunnerVpeUtils.getElementBounds(
				 getElementById(SPAN1_ID));
		 assertTrue(MessageFormat.format(THE_X_POSITION_IS_WRONG, SPAN1_ID),
				 span1Size.x >= DIV_PADDING + DIV_BORDER_WIDTH + DIV_MARGIN);
		 assertTrue(MessageFormat.format(THE_Y_POSITION_IS_WRONG, SPAN1_ID),
				 span1Size.y >= DIV_PADDING + DIV_BORDER_WIDTH + DIV_MARGIN);
		 assertTrue(MessageFormat.format(THE_HEIGHT_IS_WRONG, SPAN1_ID),
				 span1Size.height > 0);
		 assertTrue(MessageFormat.format(THE_WIDTH_IS_WRONG, SPAN1_ID),
				 span1Size.width > 0);
		 
	}

	public nsIDOMElement getElementById(String id) {
		return vpeController.getXulRunnerEditor()
				.getDOMDocument().getElementById(id);
	}
}
