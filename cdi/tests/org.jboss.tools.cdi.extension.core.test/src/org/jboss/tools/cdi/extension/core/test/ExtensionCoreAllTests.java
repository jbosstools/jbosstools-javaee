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
package org.jboss.tools.cdi.extension.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.cdi.extension.core.test.batch.BatchCoreTestSetup;
import org.jboss.tools.cdi.extension.core.test.batch.BatchValidationTest;
import org.jboss.tools.cdi.extension.core.test.jms.JMSContextTest;
import org.jboss.tools.cdi.extension.core.test.jms.JMSContextTestSetup;
import org.jboss.tools.common.base.test.validation.ValidationExceptionTest;

/**
 * @author Viacheslav Kabanovich
 */
public class ExtensionCoreAllTests {
	public static String PLUGIN_ID = "org.jboss.tools.cdi.extension.core.test"; //$NON-NLS-1$

	public static Test suite() {
		JavaModelManager.getIndexManager().disable();

		ValidationFramework.getDefault().suspendAllValidation(true);

		ValidationExceptionTest.initLogger();

		TestSuite suiteAll = new TestSuite("CDI Extensions Tests"); //$NON-NLS-1$

		TestSuite suite = new TestSuite("Deltaspike Core Project Tests"); //$NON-NLS-1$
		suite.addTestSuite(JMSContextTest.class);

		suiteAll.addTest(new JMSContextTestSetup(suite));

		suite = new TestSuite("Batch Core Project Tests");
		suite.addTestSuite(BatchValidationTest.class);
		suiteAll.addTest(new BatchCoreTestSetup(suite));

		suiteAll.addTestSuite(ValidationExceptionTest.class); // This test should be added last!

		return suiteAll;
	}
}
