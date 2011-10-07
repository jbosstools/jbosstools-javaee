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
package org.jboss.tools.cdi.seam.config.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.cdi.seam.config.core.test.v30.CDISeamConfigCoreAllTests30;
import org.jboss.tools.common.base.test.validation.ValidationExceptionTest;

/**
 * @author Viacheslav Kabanovich
 */
public class CDISeamConfigCoreAllTests {

	public static Test suite() {
		// it could be done here because it is not needed to be enabled back
		JavaModelManager.getIndexManager().disable();

		ValidationFramework.getDefault().suspendAllValidation(true);

		ValidationExceptionTest.initLogger();

		TestSuite suiteAll = new TestSuite("CDI Config Core Tests");
		
		suiteAll.addTest(CDISeamConfigCoreAllTests30.suite());

		TestSuite suite31 = new TestSuite("CDI Config Core 3.1 Tests");

		TestSuite suiteCore = new TestSuite("CDI Config Model Tests");
		suiteCore.addTestSuite(ParserTest.class);
		suiteCore.addTestSuite(ExtensionTest.class);
		suiteCore.addTestSuite(SeamDefinitionsTest.class);
		suiteCore.addTestSuite(SeamBeansTest.class);
		suiteCore.addTestSuite(ConfigBeansInjectionTest.class);
		suite31.addTest(new SeamConfigTestSetup(suiteCore));

		TestSuite suiteValidation = new TestSuite("CDI Config Validation Tests");
		suiteValidation.addTestSuite(SeamConfigValidationTest.class);
		suite31.addTest(new SeamConfigValidationTestSetup(suiteValidation));

		suiteAll.addTest(suite31);

		suiteAll.addTestSuite(ValidationExceptionTest.class); // This test should be added last!

		return suiteAll;
	}
}