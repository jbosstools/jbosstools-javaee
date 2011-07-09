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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.vpe.base.test.VpeTestSetup;

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
	public static final String IMPORT_I18N_PROJECT_NAME = "i18nTest"; //$NON-NLS-1$
	public static final String IMPORT_NATURES_CHECKER_PROJECT = "naturesCheckTest"; //$NON-NLS-1$
	public static final String IMPORT_JSF_LOCALES_PROJECT_NAME = "jsfLocales"; //$NON-NLS-1$
	public static final String IMPORT_JBIDE5460_PROJECT_NAME = "JBIDE5460TestProject"; //$NON-NLS-1$
	public static final String IMPORT_TEST_WITH_2_URL_PATTERNS_PROJECT_NAME = "TestWith2URLPatterns"; //$NON-NLS-1$

	public static Test suite() {

		TestSuite suite = new TestSuite("Tests for Vpe Jsf components"); //$NON-NLS-1$
		// $JUnit-BEGIN$
		/*
		 * Content tests
		 */
		suite.addTestSuite(JsfComponentContentTest.class) ;
		suite.addTestSuite(Jsf20ComponentContentTest.class);
		suite.addTest(JsfAllImportantTests.suite());
	
		// $JUnit-END$
		return new VpeTestSetup(suite);
	}
}
