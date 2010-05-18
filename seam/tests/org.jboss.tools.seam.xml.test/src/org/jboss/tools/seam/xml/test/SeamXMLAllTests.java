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
package org.jboss.tools.seam.xml.test;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author V.Kabanovich
 *
 */
public class SeamXMLAllTests {
	public static final String PLUGIN_ID = "org.jboss.tools.seam.xml";

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.setName("All tests for " + PLUGIN_ID);
		suite.addTest(new SeamXMLTestSetup(SeamXMLTestSuite.suite()));

		return suite;
	}
}