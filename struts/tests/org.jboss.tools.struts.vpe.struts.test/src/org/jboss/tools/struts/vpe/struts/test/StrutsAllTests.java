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
package org.jboss.tools.struts.vpe.struts.test;

import org.jboss.tools.vpe.ui.test.VpeTestSetup;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Class for testing all RichFaces components
 * 
 * @author dazarov
 * 
 */

public class StrutsAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for Vpe Struts components"); //$NON-NLS-1$
		// $JUnit-BEGIN$
		suite.addTestSuite(StrutsHTMLComponentTest.class);
		suite.addTestSuite(StrutsBeanComponentTest.class);
		suite.addTestSuite(StrutsLogicComponentTest.class);
		suite.addTestSuite(StrutsFormComponentTest.class);
		suite.addTestSuite(StrutsNestedComponentTest.class);
		suite.addTestSuite(StrutsTilesComponentTest.class);
		//cleanUpTests();
		// $JUnit-END$
		return new VpeTestSetup(suite);
	}
}
