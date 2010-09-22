/*******************************************************************************
 * Copyright (c) 2010 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.test;

import org.jboss.tools.jsf.ca.test.JsfJspJbide1704Test;
import org.jboss.tools.jsf.ca.test.JsfJspJbide1717Test;
import org.jboss.tools.jsf.ca.test.JsfJspJbide6259Test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JsfCATests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.jboss.tools.jsf.test");

		suite.addTestSuite(JsfJspJbide6259Test.class);
		suite.addTestSuite(JsfJspJbide1704Test.class);
 		suite.addTestSuite(JsfJspJbide1717Test.class);
	
		return suite;
	}
}
