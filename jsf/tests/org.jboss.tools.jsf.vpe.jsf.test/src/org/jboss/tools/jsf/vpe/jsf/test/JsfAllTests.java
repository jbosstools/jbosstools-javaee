/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Class for testing all RichFaces components
 * 
 * @author sdzmitrovich
 * 
 */

public class JsfAllTests {

	private final static String TEST_PROJECT_PATH = "/jsfTest";

	private static void prepareTests() {

		TestJsfUtil.importJsfPages(JsfTestPlugin.getPluginResourcePath()
				+ TEST_PROJECT_PATH);

	}

	public static Test suite() {
		// prepare tests
		prepareTests();

		TestSuite suite = new TestSuite("Tests for Vpe Jsf components"); // $NON-NLS-1$
		// $JUnit-BEGIN$

		suite.addTestSuite(JsfComponentTest.class);
		suite.addTestSuite(JsfJbideTest.class);

		// $JUnit-END$
		return suite;

	}

}
