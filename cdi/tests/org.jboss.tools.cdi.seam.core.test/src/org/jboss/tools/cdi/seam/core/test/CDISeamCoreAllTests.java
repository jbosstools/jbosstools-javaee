/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.seam.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.cdi.seam.core.test.international.BundleModelTest;
import org.jboss.tools.cdi.seam.core.test.international.CACdiAddInfoELMessagesTest;
import org.jboss.tools.cdi.seam.core.test.international.SeamResourceBundleCdiElResolverTest;
import org.jboss.tools.cdi.seam.core.test.international.SeamResourceBundleELTooltipTest;
import org.jboss.tools.cdi.seam.core.test.international.SeamResourceBundlesTest;
import org.jboss.tools.cdi.seam.core.test.jms.SeamJmsValidationTest;
import org.jboss.tools.cdi.seam.core.test.persistence.SeamPersistenceTest;
import org.jboss.tools.cdi.seam.core.test.persistence.SeamPersistenceTestSetup;
import org.jboss.tools.cdi.seam.core.test.rest.SeamRestValidationTest;
import org.jboss.tools.cdi.seam.core.test.servlet.SeamServletValidationTest;
import org.jboss.tools.common.base.test.validation.ValidationExceptionTest;

/**
 * @author Alexey Kazakov
 */
public class CDISeamCoreAllTests {
	public static String PLUGIN_ID = "org.jboss.tools.cdi.seam.core.test";

	public static Test suite() {

		ValidationFramework.getDefault().suspendAllValidation(true);

		ValidationExceptionTest.initLogger();

		TestSuite suiteAll = new TestSuite("Seam Core Tests");

		TestSuite suite = new TestSuite("Seam Core Project Tests");
		suite.addTestSuite(SeamServletValidationTest.class);
		suite.addTestSuite(SeamResourceBundlesTest.class);
		suite.addTestSuite(SeamJmsValidationTest.class);
		suite.addTestSuite(SeamResourceBundleCdiElResolverTest.class);
		suite.addTestSuite(SeamResourceBundleELTooltipTest.class);
		suite.addTestSuite(SeamRestValidationTest.class);
		suite.addTestSuite(CACdiAddInfoELMessagesTest.class);
		suite.addTestSuite(BundleModelTest.class); // should be the last in this suite because it removes/adds seam-international.jar

		suiteAll.addTest(new SeamCoreTestSetup(suite));

		suite = new TestSuite("Seam Persistence Project Tests");
		suite.addTestSuite(SeamPersistenceTest.class);
		suiteAll.addTest(new SeamPersistenceTestSetup(suite));

		suiteAll.addTestSuite(ValidationExceptionTest.class); // This test should be added last!

		return suiteAll;
	}
}
