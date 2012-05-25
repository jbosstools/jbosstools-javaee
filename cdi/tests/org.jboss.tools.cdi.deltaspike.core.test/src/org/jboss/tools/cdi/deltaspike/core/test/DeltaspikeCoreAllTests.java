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
import org.jboss.tools.cdi.deltaspike.core.test.validation.DeltaspikeValidationTest;
import org.jboss.tools.common.base.test.validation.ValidationExceptionTest;

/**
 * @author Alexey Kazakov
 */
public class DeltaspikeCoreAllTests {
	public static String PLUGIN_ID = "org.jboss.tools.cdi.deltaspike.core.test";

	public static Test suite() {
		JavaModelManager.getIndexManager().disable();

		ValidationFramework.getDefault().suspendAllValidation(true);

		ValidationExceptionTest.initLogger();

		TestSuite suiteAll = new TestSuite("Deltasipke Core Tests");

		TestSuite suite = new TestSuite("Deltaspike Core Project Tests");
		suite.addTestSuite(DeltaspikeBeansTest.class);
		suite.addTestSuite(DeltaspikeValidationTest.class);

		suiteAll.addTest(new DeltaspikeCoreTestSetup(suite));

		suiteAll.addTestSuite(ValidationExceptionTest.class); // This test should be added last!

		return suiteAll;
	}
}