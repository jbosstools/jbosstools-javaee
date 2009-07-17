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
package org.jboss.tools.struts.vpe.struts.test;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Class for testing tiles struts components
 * 
 * @author dazarov
 * 
 */
public class StrutsTilesComponentTest extends VpeTest {

	// import project name
	static final String IMPORT_PROJECT_NAME = "StrutsTest";

	public StrutsTilesComponentTest(String name) {
		super(name);
	}

	/*
	 * Struts Tiles test cases
	 */

	public void testAdd() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/tiles/add.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testDefinition() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/tiles/definition.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testGet() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/tiles/get.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testGetAsString() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/tiles/getAsString.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testImportAttribute() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/tiles/importAttribute.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testInitComponentDefinitions() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/tiles/initComponentDefinitions.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testInsert() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/tiles/insert.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testPutList() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/tiles/putList.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testUserAttribute() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/tiles/userAttribute.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
}
