/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsp.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.vpe.ui.test.VpeTestSetup;
import org.jboss.tools.vpe.ui.test.beans.ImportBean;

public class JSPAllTests {
    public static Test suite() {

	TestSuite suite = new TestSuite("Tests for Vpe JSP components"); //$NON-NLS-1$
	// $JUnit-BEGIN$

	suite.addTestSuite(JSPComponentTest.class);

	// $JUnit-END$
	List<ImportBean> importProjects = new ArrayList<ImportBean>();
	ImportBean importBean = new ImportBean();
	importBean.setImportProjectName(JSPComponentTest.IMPORT_PROJECT_NAME);
	importBean.setImportProjectPath(JSPTestPlugin.getPluginResourcePath());
	importProjects.add(importBean);
	return new VpeTestSetup(suite,importProjects);

    }
}
