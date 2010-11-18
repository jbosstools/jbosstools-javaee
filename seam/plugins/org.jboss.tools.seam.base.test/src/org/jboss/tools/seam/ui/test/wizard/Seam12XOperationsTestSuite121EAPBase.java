package org.jboss.tools.seam.ui.test.wizard;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;


public class Seam12XOperationsTestSuite121EAPBase {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.setName("Seam 1.2.X Operations Tests");
		suite.addTest(
				new Seam12EARNewOperationTestSetup(
						new TestSuite(Seam12EARNewOperationTest.class)));
		suite.addTest(
				new Seam12WARNewOperationTestSetup(
						new TestSuite(Seam12WARNewOperationTest.class)));
		return suite;
	}

	public static class Seam12EARNewOperationTestSetup extends TestSetup {
		Seam12EARNewOperationTest delegate = new Seam12EARNewOperationTest("delegate");
		/**
		 * @param test
		 */
		public Seam12EARNewOperationTestSetup(Test test) {
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

	public static class Seam12WARNewOperationTestSetup extends TestSetup {
		Seam12WARNewOperationTest delegate = new Seam12WARNewOperationTest("delegate");
		/**
		 * @param test
		 */
		public Seam12WARNewOperationTestSetup(Test test) {
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

	public Seam12XOperationsTestSuite121EAPBase() {
		super();
	}

}