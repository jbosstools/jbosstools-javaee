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


import junit.framework.TestSuite;

public class Seam12XTestSuite {
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Seam 1.2.X tests");
		suite.addTestSuite(SeamFacetInstallDelegateTest.class);
		return suite;
	}
}
