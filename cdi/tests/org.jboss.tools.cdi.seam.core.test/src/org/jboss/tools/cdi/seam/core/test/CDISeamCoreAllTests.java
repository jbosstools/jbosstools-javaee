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
import org.jboss.tools.cdi.seam.core.test.international.BundleModelTest;
import org.jboss.tools.cdi.seam.core.test.international.SeamResourceBundlesTest;
import org.jboss.tools.cdi.seam.core.test.persistence.SeamPersistenceTest;
import org.jboss.tools.cdi.seam.core.test.persistence.SeamPersistenceTestSetup;
import org.jboss.tools.cdi.seam.core.test.servlet.SeamServletValidationTest;

/**
 * @author Alexey Kazakov
 */
public class CDISeamCoreAllTests {
	public static String PLUGIN_ID = "org.jboss.tools.cdi.seam.core.test";

	public static Test suite() {
		JavaModelManager.getIndexManager().disable();

		TestSuite suiteAll = new TestSuite("Seam Core Tests");

		suiteAll.addTestSuite(SeamResourceBundlesTest.class);
		suiteAll.addTestSuite(BundleModelTest.class);

		TestSuite suite = new TestSuite("Seam Core Project Tests");
		suiteAll.addTest(new SeamCoreTestSetup(suite));
		suiteAll.addTestSuite(SeamServletValidationTest.class);
		
		suite = new TestSuite("Seam Persistence Project Tests");
		suiteAll.addTest(new SeamPersistenceTestSetup(suite));
		suiteAll.addTestSuite(SeamPersistenceTest.class);
		return suiteAll;
	}
}