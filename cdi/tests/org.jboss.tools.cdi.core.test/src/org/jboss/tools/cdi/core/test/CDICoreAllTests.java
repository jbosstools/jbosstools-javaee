/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.jboss.tools.cdi.core.test.tck.NamedBeanRefactoringTest;
import org.jboss.tools.tests.AbstractPluginsLoadTest;

/**
 * @author Alexey Kazakov
 */
public class CDICoreAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("CDI Core Tests");
		suite.addTest(new CDICoreTestSetup(CDICoreTestSuite.suite()));
		suite.addTest(NamedBeanRefactoringTest.suite());

		return new DisableJavaIndexingSetup(suite);
	}

	public class CDIPluginsLoadTest extends AbstractPluginsLoadTest {
		public void testBundlesAreLoadedForSeamFeature(){
			testBundlesAreLoadedFor("org.jboss.tools.cdi.feature");
		}
	}

	public static class DisableJavaIndexingSetup extends TestSetup {

		public DisableJavaIndexingSetup(Test test) {
			super(test);
		}
		
		@Override
		protected void setUp() throws Exception {
			JavaModelManager.getIndexManager().disable();
		}

		@Override
		protected void tearDown() throws Exception {
			JavaModelManager.getIndexManager().enable();
		}
	}
}