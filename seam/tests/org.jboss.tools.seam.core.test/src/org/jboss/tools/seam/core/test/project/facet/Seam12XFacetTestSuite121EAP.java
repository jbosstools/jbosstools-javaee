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


import org.eclipse.wst.validation.ValidationFramework;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

public class Seam12XFacetTestSuite121EAP {
	public static Test suite() {
		TestSuite suite = new TestSuite("Seam 1.2.X tests");
		suite.addTest(new TestSetup(new TestSuite(SeamFacetInstallDelegateTest.class)) {

			private boolean suspendAllValidation ;

			@Override
			protected void setUp() throws Exception {
				suspendAllValidation = ValidationFramework.getDefault().isSuspended();
				ValidationFramework.getDefault().suspendAllValidation(true);
			}

			@Override
			protected void tearDown() throws Exception {
				ValidationFramework.getDefault().suspendAllValidation(suspendAllValidation);
			}
		});
		return suite;
	}
}
