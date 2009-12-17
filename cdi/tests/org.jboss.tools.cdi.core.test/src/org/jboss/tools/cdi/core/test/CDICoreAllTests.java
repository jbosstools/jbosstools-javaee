/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.tests.AbstractPluginsLoadTest;

/**
 * @author Alexey Kazakov
 */
public class CDICoreAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("CDI Core Tests");
		suite.addTestSuite(TCKTest.class);
		return suite;
	}

	public class CDIPluginsLoadTest extends AbstractPluginsLoadTest {
		public void testBundlesAreLoadedForSeamFeature(){
			testBundlesAreLoadedFor("org.jboss.tools.cdi.feature");
		}
	}
}