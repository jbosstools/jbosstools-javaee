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
package org.jboss.tools.cdi.deltaspike.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.cdi.deltaspike.core.test.validation.AYTDeltaspikeInjectionValidationTest;
import org.jboss.tools.cdi.deltaspike.core.test.validation.DeltaspikeInjectionValidationTest;
import org.jboss.tools.cdi.deltaspike.core.test.validation.DeltaspikeValidationTest;
import org.jboss.tools.common.base.test.validation.ValidationExceptionTest;

/**
 * @author Alexey Kazakov
 */
public class DeltaspikeCoreAllTests {
	public static String PLUGIN_ID = "org.jboss.tools.cdi.deltaspike.core.test"; //$NON-NLS-1$

	public static Test suite() {
		JavaModelManager.getIndexManager().disable();

		ValidationFramework.getDefault().suspendAllValidation(true);

		ValidationExceptionTest.initLogger();

		TestSuite suiteAll = new TestSuite("Deltasipke Core Tests"); //$NON-NLS-1$

		TestSuite suite = new TestSuite("Deltaspike Core Project Tests"); //$NON-NLS-1$
		suite.addTestSuite(DeltaspikeBeansTest.class);

		// Validation tests
		suite.addTestSuite(DeltaspikeValidationTest.class);
		suite.addTestSuite(DeltaspikeInjectionValidationTest.class);
		suite.addTestSuite(AYTDeltaspikeInjectionValidationTest.class);

		suiteAll.addTest(new DeltaspikeCoreTestSetup(suite));

		suiteAll.addTestSuite(ValidationExceptionTest.class); // This test should be added last!

		return suiteAll;
	}
}