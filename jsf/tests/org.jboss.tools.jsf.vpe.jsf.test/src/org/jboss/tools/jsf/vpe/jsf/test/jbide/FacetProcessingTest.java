/******************************************************************************* 
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.ui.test.ComponentContentTest;

/**
 *JUnit for https://jira.jboss.org/jira/browse/JBIDE-5768
 * 
 * @author mareshkau
 *
 */
public class FacetProcessingTest extends ComponentContentTest {

	public FacetProcessingTest(String name) {
		super(name);
	}

	public void testFacetProcessingTest() throws Throwable{
		performContentTest("JBIDE/5768/test.xhtml"); //$NON-NLS-1$

		
	}
	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_PROJECT_NAME;
	}

}
