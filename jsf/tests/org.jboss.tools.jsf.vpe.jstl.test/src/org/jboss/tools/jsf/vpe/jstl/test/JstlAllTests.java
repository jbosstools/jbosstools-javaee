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
package org.jboss.tools.jsf.vpe.jstl.test;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.tests.ImportBean;
import org.jboss.tools.vpe.ui.test.VpeTestSetup;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JstlAllTests {
    
    public static final String IMPORT_PROJECT_NAME = "jstlTests"; //$NON-NLS-1$
    
    public static Test suite() {
	
	TestSuite suite = new TestSuite("Tests for Vpe JSTL components"); //$NON-NLS-1$
	
	suite.addTestSuite(JstlComponentContentTest.class);
	
	List<ImportBean> projectToImport = new ArrayList<ImportBean>();
	ImportBean importBean = new ImportBean();
	importBean.setImportProjectName(JstlAllTests.IMPORT_PROJECT_NAME);
	importBean.setImportProjectPath(JstlTestPlugin.getPluginResourcePath());
	projectToImport.add(importBean);

	return new VpeTestSetup(suite, projectToImport);
    }
}
