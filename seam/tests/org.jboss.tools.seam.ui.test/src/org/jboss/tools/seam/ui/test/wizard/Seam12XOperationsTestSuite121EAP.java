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

import junit.framework.TestSuite;

/**
 * @author eskimo
 *
 */
public class Seam12XOperationsTestSuite121EAP {
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.setName("Seam 1.2.X Operations Tests");
		suite.addTestSuite(Seam12EARNewOperationTest.class);
		suite.addTestSuite(Seam12WARNewOperationTest.class);
		return suite;
	}
}
