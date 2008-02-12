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
package org.jboss.tools.jsf.vpe.richfaces.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.JBIDE1579Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.JBIDE1613Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.Jbide1580Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.Jbide1614Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.Jbide1639Test;
import org.jboss.tools.jsf.vpe.richfaces.test.jbide.Jbide1682Test;
import org.jboss.tools.vpe.ui.test.VpeTestSetup;
import org.jboss.tools.vpe.ui.test.beans.ImportBean;

/**
 * Class for testing all RichFaces components
 * 
 * @author dsakovich@exadel.com
 * 
 */

public class RichFacesAllTests {

    public static Test suite() {
	TestSuite suite = new TestSuite("Tests for Vpe RichFaces components"); // $NON-NLS-1$
	// $JUnit-BEGIN$

	suite.addTestSuite(RichFacesComponentTest.class);
	suite.addTestSuite(JBIDE1579Test.class);
	suite.addTestSuite(Jbide1580Test.class);
	suite.addTestSuite(JBIDE1613Test.class);
	suite.addTestSuite(Jbide1614Test.class);
	suite.addTestSuite(Jbide1639Test.class);
	suite.addTestSuite(Jbide1682Test.class);
	
	// $JUnit-END$

	List<ImportBean> projectToImport = new ArrayList<ImportBean>();
	ImportBean importBean = new ImportBean();
	importBean
		.setImportProjectName(RichFacesComponentTest.IMPORT_PROJECT_NAME);
	importBean.setImportProjectPath(RichFacesTestPlugin
		.getPluginResourcePath());
	projectToImport.add(importBean);

	return new VpeTestSetup(suite, projectToImport);

    }

}
