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
package org.jboss.tools.cdi.seam.solder.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.cdi.seam.solder.core.test.aytvalidation.AYTSeamServletValidationTest;
import org.jboss.tools.cdi.seam.solder.core.test.v30.CDISeamSolderCoreAllTests30;
import org.jboss.tools.common.base.test.validation.ValidationExceptionTest;
/**
 * @author Viacheslav Kabanovich
 */
public class CDISeamSolderCoreAllTests {

	public static Test suite() {
		// it could be done here because it is not needed to be enabled back
		JavaModelManager.getIndexManager().disable();

		ValidationFramework.getDefault().suspendAllValidation(true);

		ValidationExceptionTest.initLogger();

		TestSuite suiteAll = new TestSuite("CDI Solder Core Tests");

		suiteAll.addTest(CDISeamSolderCoreAllTests30.suite());

		TestSuite suite31 = new TestSuite("CDI Solder Core 3.1 Tests");

		SeamSolderTestSetup suite = new SeamSolderTestSetup(suite31);

		suite31.addTestSuite(GenericBeanTest.class);
		suite31.addTestSuite(GenericBeanValidationTest.class);
		suite31.addTestSuite(BeanNamingTest.class);
		suite31.addTestSuite(VetoTest.class);
		suite31.addTestSuite(ExactTest.class);
		suite31.addTestSuite(MessageLoggerTest.class);
		suite31.addTestSuite(ServiceHandlerTest.class);
		suite31.addTestSuite(DefaultBeanTest.class);
		suite31.addTestSuite(DefaultBeanValidationTest.class);
		suite31.addTestSuite(UnwrapsTest.class);
		suite31.addTestSuite(SeamServletValidationTest.class);
		suite31.addTestSuite(AYTSeamServletValidationTest.class);

		suiteAll.addTest(suite);

		suiteAll.addTestSuite(ValidationExceptionTest.class); // This test should be added last!
		
		return suiteAll;
	}
}