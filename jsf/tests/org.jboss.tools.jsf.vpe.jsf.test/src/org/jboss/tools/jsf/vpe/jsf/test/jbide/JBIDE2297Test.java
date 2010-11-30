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
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;

/**
 * Test case for 
 * 
 * @author mareshkau
 *
 */
public class JBIDE2297Test extends VpeTest{

	private static final String TEST_PAGE_NAME1 = "JBIDE/2297/JBIDE-2297.xhtml"; //$NON-NLS-1$

	private static final String TEST_PAGE_NAME2 ="JBIDE/2297/limitedEntry.jsp"; //$NON-NLS-1$
	
	public JBIDE2297Test(String name) {
		super(name);
	}
	
	public void testJBIDE2297() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(TEST_PAGE_NAME1,JsfAllTests.IMPORT_PROJECT_NAME)); 
	}
	
	public void testJBIDE2297_jsp() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(TEST_PAGE_NAME2,JsfAllTests.IMPORT_PROJECT_NAME)); 
	}
}
