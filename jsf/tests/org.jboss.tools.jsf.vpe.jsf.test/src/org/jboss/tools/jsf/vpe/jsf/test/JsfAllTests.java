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

import org.jboss.tools.jsf.vpe.jsf.test.jbide.ChangeMessageBundleTest_JBIDE5818;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.ContextMenuDoubleInsertionTest_JBIDE3888;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.EditFontFamilyTest_JBIDE5872;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.ExceptionInVPEComments_JBIDE5143;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.FacetProcessingTest;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1105Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1460Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE1479Test;
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
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE5920Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE675Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE788Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JBIDE924Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JSF2ValidatorTest;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide1467Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide1501Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide1568Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide1718Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide2170Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.JsfJbide2362Test;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.MessageResolutionInPreviewTabTest;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.MozDirtyTest_JBIDE5105;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.NullPointerWithStyleProperty_JBIDE5193;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.OpenOnCssClassTest_JBIDE4775;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.OpenOnInJarPackageFragment_JBIDE5682;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.OpenOnInsideJspRoot_JBIDE4852;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.OpenOnJsf20Test_JBIDE5382;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.OpenOnTLDPackedInJar_JBIDE5693;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.PreferencesForEditors_JBIDE5692;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.RefreshBundles_JBIDE5460;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.RenderFacetAndInsertChildrenTest;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.SelectAllAndCut_JBIDE4853;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.SelectWholeElement_JBIDE4713;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.SourceDomUtilTest;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.TestContextPathResolution;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.TestFViewLocaleAttribute_JBIDE5218;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.TestForUsingComponentsLibrariesWithDefaultNamespace;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.TestOpenOnForXhtmlFiles_JBIDE5577;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.UnclosedELExpressionTest;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.VPERefreshTest;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.VisualRefreshComment_JBIDE6067;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.VpeI18nTest_JBIDE4887;
import org.jboss.tools.jsf.vpe.jsf.test.jbide.XulRunnerVpeUtilsTest;
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
