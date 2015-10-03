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

import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.base.test.ComponentContentTest;
import org.junit.Test;

/**
 * Junit test for https://jira.jboss.org/jira/browse/JBIDE-6064
 * 
 * @author mareshkau
 *
 */
public class UnclosedELExpressionTest extends ComponentContentTest {

	public UnclosedELExpressionTest() {
	}

	@Test
	public void testCheckContetnForDefaultNamespace() throws Throwable{
		performContentTest("JBIDE/6064/jbide6064.xhtml"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.ui.test.ComponentContentTest#getTestProjectName()
	 */
	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_PROJECT_NAME;
	}

}
