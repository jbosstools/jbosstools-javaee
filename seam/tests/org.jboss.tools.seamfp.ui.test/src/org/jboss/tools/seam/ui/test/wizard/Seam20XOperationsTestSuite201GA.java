/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.test.wizard;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author eskimo
 */
public class Seam20XOperationsTestSuite201GA {
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.setName("Seam 2.0.X Operations Test");
		suite.addTest(
				new Seam20EARNewOperationTestSetup(
						new TestSuite(Seam20EARNewOperationTest.class)));
		suite.addTest(
				new Seam20WARNewOperationTestSetup(
						new TestSuite(Seam20WARNewOperationTest.class)));
		return suite;
	}
	
	public static class Seam20EARNewOperationTestSetup extends TestSetup {
		Seam20EARNewOperationTest delegate = new Seam20EARNewOperationTest("delegate");
		
		/**
		 * @param test
		 */
		public Seam20EARNewOperationTestSetup(Test test) {
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
	
	public static class Seam20WARNewOperationTestSetup extends TestSetup {
		Seam20WARNewOperationTest delegate = new Seam20WARNewOperationTest("delegate");
		/**
		 * @param test
		 */
		public Seam20WARNewOperationTestSetup(Test test) {
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
