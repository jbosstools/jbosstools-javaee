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
package org.jboss.tools.jsf.text.ext.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.test.util.ProjectImportTestSetup;

public class JsfTextExtAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(JsfTextExtAllTests.class.getName());
		suite.addTest(new ProjectImportTestSetup(new TestSuite(OpenOnsTest.class),
				"org.jboss.tools.jsf.text.ext.test",
				new String[]{"projects/HiperlinksTestProject"},
				new String[]{"HiperlinksTestProject"}));

		return suite;
	}
}