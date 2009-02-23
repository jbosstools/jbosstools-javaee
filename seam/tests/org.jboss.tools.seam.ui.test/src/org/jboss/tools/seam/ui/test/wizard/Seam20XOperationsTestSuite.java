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
 * 
 * @author eskimo
 */
public class Seam20XOperationsTestSuite {
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.setName("Seam 2.0.X Operations Test");
		suite.addTestSuite(Seam20EARNewOperationTest.class);
		suite.addTestSuite(Seam20WARNewOperationTest.class);
		return suite;
	}
	
}
