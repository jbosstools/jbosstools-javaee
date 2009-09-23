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

import org.jboss.tools.jsf.vpe.jsf.test.jbide.ContextMenuDoubleInsertionTest_JBIDE3888;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1105Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1460Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1479Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1484Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1494Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1615Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1720Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1744Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1805Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2010Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2119Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2219Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2297Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2354Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2434Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2505Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2526Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2550Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2582Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2584Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2594Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2624Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2774Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2828Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE2979Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3030Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3127Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3144Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3163Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3197Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3247Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3376Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3396Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3441Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3473Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3482Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3519Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3617Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3632Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3650Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3734Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE3969Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE4037Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE4179Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE4337Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE4373Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE4509Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE4510Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE4534Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE675Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE788Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE924Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide1467Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide1501Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide1568Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide1718Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide2170Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide2362Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.OpenOnCssClassTest_JBIDE4775;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.SelectAllAndCut_JBIDE4853;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.SelectWholeElement_JBIDE4713;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.VPERefreshTest;
import org.jboss.tools.tests.ImportBean;
import org.jboss.tools.vpe.ui.test.VpeTestSetup;

/**
 * Class for testing all RichFaces components
 * 
 * @author sdzmitrovich
 * 
 */

public class JsfAllTests {

	public static final String IMPORT_PROJECT_NAME = "jsfTest"; //$NON-NLS-1$
	public static final String IMPORT_JSF_20_PROJECT_NAME = "jsf2test"; //$NON-NLS-1$
	public static final String IMPORT_CUSTOM_FACELETS_PROJECT = "customFaceletsTestProject";//$NON-NLS-1$
	public static final String IMPORT_JBIDE3247_PROJECT_NAME = "JBIDE3247"; //$NON-NLS-1$
	
	public static Test suite() {

		TestSuite suite = new TestSuite("Tests for Vpe Jsf components"); //$NON-NLS-1$
		// $JUnit-BEGIN$
		suite.addTestSuite(JsfComponentTest.class);
		suite.addTestSuite(Jsf20ComponentContentTest.class);
		suite.addTestSuite(JBIDE3519Test.class);
		suite.addTestSuite(ContextMenuDoubleInsertionTest_JBIDE3888.class);
		suite.addTestSuite(SelectAllAndCut_JBIDE4853.class);
		suite.addTestSuite(SelectWholeElement_JBIDE4713.class);
		suite.addTestSuite(JBIDE4037Test.class);
		suite.addTestSuite(JBIDE3734Test.class);
		suite.addTestSuite(JBIDE3617Test.class);
		suite.addTestSuite(JBIDE3473Test.class);
		suite.addTestSuite(JBIDE3441Test.class);
		suite.addTestSuite(JsfJbide1467Test.class);
		suite.addTestSuite(JsfJbide1501Test.class);
		suite.addTestSuite(JBIDE1484Test.class);
		suite.addTestSuite(JsfJbide1568Test.class);
		suite.addTestSuite(JBIDE1615Test.class);
		suite.addTestSuite(JBIDE1479Test.class);
		suite.addTestSuite(JBIDE788Test.class);
		suite.addTestSuite(JBIDE1105Test.class);
		suite.addTestSuite(JBIDE1744Test.class);
		suite.addTestSuite(JBIDE1460Test.class);
		suite.addTestSuite(JBIDE1720Test.class);
		suite.addTestSuite(JsfJbide1718Test.class);
		suite.addTestSuite(JBIDE1494Test.class);
		suite.addTestSuite(JBIDE2297Test.class);
		suite.addTestSuite(JsfJbide2170Test.class);
		suite.addTestSuite(JBIDE2434Test.class);
		suite.addTestSuite(JsfJbide2362Test.class);
		suite.addTestSuite(JBIDE2119Test.class);
		suite.addTestSuite(JBIDE2219Test.class);
		suite.addTestSuite(JBIDE2505Test.class);
		suite.addTestSuite(JBIDE2584Test.class);
	    suite.addTestSuite(ElPreferencesTestCase.class);
	    suite.addTestSuite(JBIDE2010Test.class);
	    suite.addTestSuite(JBIDE2582Test.class);
	    suite.addTestSuite(JBIDE2594Test.class);
		suite.addTestSuite(JBIDE924Test.class);
		suite.addTestSuite(JBIDE2526Test.class);
		suite.addTestSuite(JBIDE2624Test.class);
		suite.addTestSuite(JBIDE1805Test.class);
		suite.addTestSuite(JsfComponentContentTest.class);
		suite.addTestSuite(JBIDE2774Test.class);
		suite.addTestSuite(JBIDE2828Test.class);
		suite.addTestSuite(JBIDE3030Test.class);
		suite.addTestSuite(JBIDE2979Test.class);
		suite.addTestSuite(JBIDE3127Test.class);
		suite.addTestSuite(JBIDE3144Test.class);
		suite.addTestSuite(JBIDE2354Test.class);
		suite.addTestSuite(JBIDE3163Test.class);
		suite.addTestSuite(JBIDE3376Test.class);
		suite.addTestSuite(JBIDE3396Test.class);
		suite.addTestSuite(JBIDE3482Test.class);
		suite.addTestSuite(JBIDE3632Test.class);
		suite.addTestSuite(JBIDE3650Test.class);
		suite.addTestSuite(JBIDE3197Test.class);
		suite.addTestSuite(JBIDE4373Test.class);
		suite.addTestSuite(JBIDE675Test.class);
		suite.addTestSuite(JBIDE3969Test.class);
		suite.addTestSuite(JBIDE4337Test.class);
		suite.addTestSuite(JBIDE4179Test.class);
		suite.addTestSuite(JBIDE4509Test.class);
		suite.addTestSuite(JBIDE4510Test.class);
		suite.addTestSuite(JBIDE4534Test.class);
		suite.addTestSuite(JBIDE3247Test.class);
		suite.addTestSuite(JBIDE2550Test.class);
		suite.addTestSuite(OpenOnCssClassTest_JBIDE4775.class);
		suite.addTestSuite(VPERefreshTest.class);
		// $JUnit-END$
		// added by Max Areshkau
		// add here projects which should be imported for junit tests
		List<ImportBean> projectToImport = new ArrayList<ImportBean>();
		ImportBean importBeanJsf1 = new ImportBean();
		importBeanJsf1.setImportProjectName(JsfAllTests.IMPORT_PROJECT_NAME);
		importBeanJsf1.setImportProjectPath(JsfTestPlugin.getPluginResourcePath());
		projectToImport.add(importBeanJsf1);
		
		ImportBean importBeanJsf20 = new ImportBean();
		importBeanJsf20.setImportProjectName(JsfAllTests.IMPORT_JSF_20_PROJECT_NAME);
		importBeanJsf20.setImportProjectPath(JsfTestPlugin.getPluginResourcePath());
		projectToImport.add(importBeanJsf20);

		ImportBean customFaceletsTestProject = new ImportBean();
		customFaceletsTestProject.setImportProjectName(JsfAllTests.IMPORT_CUSTOM_FACELETS_PROJECT);
		customFaceletsTestProject.setImportProjectPath(JsfTestPlugin.getPluginResourcePath());
		projectToImport.add(customFaceletsTestProject);
		
		ImportBean jbide3247TestProject = new ImportBean();
		jbide3247TestProject.setImportProjectName(JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME);
		jbide3247TestProject.setImportProjectPath(JsfTestPlugin.getPluginResourcePath());
		projectToImport.add(jbide3247TestProject);
		
		return new VpeTestSetup(suite, projectToImport);
	}
}
