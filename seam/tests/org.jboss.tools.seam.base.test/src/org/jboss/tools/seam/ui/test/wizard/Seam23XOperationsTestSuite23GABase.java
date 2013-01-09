/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.seam.ui.test.wizard;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Alexey Kazakov
 */
public class Seam23XOperationsTestSuite23GABase {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.setName("Seam 2.3 Operations Test");
		suite.addTest(
				new Seam23EARNewOperationTestSetup(
						new TestSuite(Seam23EARNewOperationTest.class)));
		suite.addTest(
				new Seam23WARNewOperationTestSetup(
						new TestSuite(Seam23WARNewOperationTest.class)));
		return suite;
	}

	public static class Seam23EARNewOperationTestSetup extends TestSetup {
		Seam23EARNewOperationTest delegate = new Seam23EARNewOperationTest("delegate");
	
		/**
		 * @param test
		 */
		public Seam23EARNewOperationTestSetup(Test test) {
			super(test);
		}
	
		@Override
		protected void setUp() throws Exception {
			delegate.setUp();
		}
	
		@Override
		protected void tearDown() throws Exception {
			delegate.tearDown();
		}
	}

	public static class Seam23WARNewOperationTestSetup extends TestSetup {
		Seam23WARNewOperationTest delegate = new Seam23WARNewOperationTest("delegate");
		/**
		 * @param test
		 */
		public Seam23WARNewOperationTestSetup(Test test) {
			super(test);
		}

		@Override
		protected void setUp() throws Exception {
			delegate.setUp();
		}

		@Override
		protected void tearDown() throws Exception {
			delegate.tearDown();
		}
	}
}