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
package org.jboss.tools.jsf.vpe.facelets.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.jsf.vpe.facelets.test.jbide.JBIDE3416Test;
import org.jboss.tools.vpe.ui.test.VpeTestSetup;

public class FaceletsAllTests {

    // import project name
    public static final String IMPORT_PROJECT_NAME = "faceletsTest"; //$NON-NLS-1$
	
    public static Test suite() {
		TestSuite suite = new TestSuite("Tests for Vpe Facelets components");
		// $JUnit-BEGIN$
	
		suite.addTestSuite(FaceletsComponentTest.class);
		suite.addTestSuite(JBIDE3416Test.class);
		suite.addTestSuite(FaceletsComponentContentTest.class);
	
		// $JUnit-END$
		return new VpeTestSetup(suite);
    }
}
