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

import org.jboss.tools.vpe.editor.menu.InsertType;
import org.jboss.tools.vpe.editor.menu.action.InsertAction2;


/**
 * Tests for JIRA issue JBIDE-3888: Taglib declaration for empty page
 * doesn't appear correctly from context menu.
 * (https://jira.jboss.org/jira/browse/JBIDE-3519 )
 * 
 * @author yradtsevich
 */
public class ContextMenuDoubleInsertionTest_JBIDE3888
		extends ContextMenuTestAbstract {
	private static final String TEST_PAGE_NAME
			= "JBIDE/3888/JBIDE-3888.jsp";		//$NON-NLS-1$
	private static final String INSERTION_ITEM_PATH
			= "%Palette%/JSF/HTML/column";		//$NON-NLS-1$
	private static final String REQUIRED_STRING
			= "http://java.sun.com/jsf/html";	//$NON-NLS-1$

	public ContextMenuDoubleInsertionTest_JBIDE3888(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Tests JBIDE-3888.
	 * <P>
	 * This method implements the following test case:
	 * <ol>
	 * <li>Open a blank JSP page.</li>
	 * <li>Open the context menu 
	 * and select Insert Into->JSF->HTML->h:column</li>
	 * <li>Delete all text.</li>
	 * <li>Open the context menu 
	 * and select Insert Into->JSF->HTML->h:column</li>
	 * </ol>
	 * EXPECTED RESULT: A taglib declaration http://java.sun.com/jsf/html
	 * and a pair of opening/closing h:column tag is inserted.<BR>
	 * ACTUAL RESULT (if the test fails):
	 * The taglib declaration is not inserted. 
	 */
	public void testDoubleInsertion() {
		final InsertAction2 firstInsertAction = new InsertAction2(
				"Insert Action", insertionItem,				//$NON-NLS-1$
				sourceEditor, InsertType.INSERT_INTO);
		firstInsertAction.run();
		textWidget.setText("");
		final InsertAction2 secondInsertAction = new InsertAction2(
				"Insert Action", insertionItem,				//$NON-NLS-1$
				sourceEditor, InsertType.INSERT_INTO);
		secondInsertAction.run();
		assertTrue(textWidget.getText()
				.contains(REQUIRED_STRING));
	}

	@Override
	protected String getInsertionItemPath() {
		return INSERTION_ITEM_PATH;
	}

	@Override
	protected String getTestPagePath() {
		return TEST_PAGE_NAME;
	}
}
