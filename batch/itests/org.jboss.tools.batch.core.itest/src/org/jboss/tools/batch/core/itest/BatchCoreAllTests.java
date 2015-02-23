/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.core.itest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.common.base.test.validation.ValidationProjectTestSetup;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author Viacheslav Kabanovich
 */
public class BatchCoreAllTests {

	public static Test suite() {
		// it could be done here because it is not needed to be enabled back
		JavaModelManager.getIndexManager().shutdown();
		try {
			ResourcesUtils.setBuildAutomatically(false);
			ValidationFramework.getDefault().suspendAllValidation(true);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		TestSuite suiteAll = new TestSuite("Batch Core Tests");

		TestSuite suite = new TestSuite("Model");
		suite.addTestSuite(BatchModelTest.class);

		ProjectImportTestSetup testSetup = new ProjectImportTestSetup(suite,
				"org.jboss.tools.batch.core.itest",
				new String[]{"projects/BatchTestProject"},
				new String[]{"BatchTestProject"});

		suiteAll.addTest(testSetup);
		
		suiteAll.addTest(new ValidationProjectTestSetup(new TestSuite(
				BatchValidatorTest.class), "org.jboss.tools.batch.core.itest", //$NON-NLS-1$
				new String[] { "projects/BatchTestProject" }, //$NON-NLS-1$
				new String[] { "BatchTestProject" })); //$NON-NLS-1$

		suite = new TestSuite("As-You-Type Validation");
		suite.addTestSuite(BatchAsYouTypeValidationTest.class);

		testSetup = new ProjectImportTestSetup(suite,
				"org.jboss.tools.batch.core.itest",
				new String[]{"projects/BatchTestProject"},
				new String[]{"BatchTestProject"});

		suiteAll.addTest(testSetup);
		return suiteAll;
	}
}
