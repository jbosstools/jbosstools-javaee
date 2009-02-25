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
package org.jboss.tools.seam.core.test.project.facet;

import org.jboss.tools.test.util.JobUtils;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author eskimo
 *
 */
public class Seam20XFacetTestSuite201GA {
	public static Test suite() {
		TestSuite suite = new TestSuite("Seam 2.0.X tests");
		suite.addTest(new Seam2FacetInstallDelegateTestSetup(new TestSuite(Seam2FacetInstallDelegateTest.class)));
		return suite;
	}
	
	public static class Seam2FacetInstallDelegateTestSetup extends TestSetup {

		Seam2FacetInstallDelegateTest delegate = new Seam2FacetInstallDelegateTest("Delegate");
		@Override
		protected void setUp() throws Exception {
			delegate.setUp();
			JobUtils.waitForIdle(50);
		}

		@Override
		protected void tearDown() throws Exception {
			delegate.tearDown();
		}

		/**
		 * @param test
		 */
		public Seam2FacetInstallDelegateTestSetup(Test test) {
			super(test);
		}

	}
}
