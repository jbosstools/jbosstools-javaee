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
package org.jboss.tools.jsf.vpe.jsf.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1105Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1479Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1484Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1615Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1744Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE788Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide1467Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide1501Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide1548Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide1568Test;
import org.jboss.tools.jsf.vpe.jsf.test.perfomance.PerfomanceTest;
import org.jboss.tools.vpe.ui.test.VpeTestSetup;
import org.jboss.tools.vpe.ui.test.beans.ImportBean;

/**
 * Class for testing all RichFaces components
 * 
 * @author sdzmitrovich
 * 
 */

public class JsfAllTests {

	public static Test suite() {

		TestSuite suite = new TestSuite("Tests for Vpe Jsf components"); // $NON-NLS-1$
		// $JUnit-BEGIN$
		suite.addTestSuite(JsfComponentTest.class);
		suite.addTestSuite(JsfJbide1467Test.class);
		suite.addTestSuite(JsfJbide1501Test.class);
		suite.addTestSuite(JBIDE1484Test.class);
		suite.addTestSuite(JsfJbide1548Test.class);
		suite.addTestSuite(JsfJbide1568Test.class);
		suite.addTestSuite(JBIDE1615Test.class);
		suite.addTestSuite(JBIDE1479Test.class);
		suite.addTestSuite(JBIDE788Test.class);
		suite.addTestSuite(JBIDE1105Test.class);
		suite.addTestSuite(JBIDE1744Test.class);		
 		
		// $JUnit-END$
		//added by Max Areshkau
		//add here projects which should be imported for junit tests
		List<ImportBean> projectToImport = new ArrayList<ImportBean>();
		ImportBean importBean = new ImportBean();
		importBean.setImportProjectName(JsfComponentTest.IMPORT_PROJECT_NAME);
 		importBean.setImportProjectPath(JsfTestPlugin.getPluginResourcePath());
 		projectToImport.add(importBean);
 		
 		// Perfomance Tests
 		//TODO dsakovich adjust perfomance tests
// 		suite.addTestSuite(PerfomanceTest.class);
//		ImportBean importPerfomanceBean = new ImportBean();
//		importPerfomanceBean.setImportProjectName(PerfomanceTest.IMPORT_PROJECT_NAME);
// 		importPerfomanceBean.setImportProjectPath(JsfTestPlugin.getPluginResourcePath());
// 		projectToImport.add(importPerfomanceBean);
		
 		return new VpeTestSetup(suite,projectToImport);

	}

}
