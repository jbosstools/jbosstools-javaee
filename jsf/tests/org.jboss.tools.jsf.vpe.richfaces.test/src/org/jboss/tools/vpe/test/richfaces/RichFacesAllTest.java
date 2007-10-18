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
package org.jboss.tools.vpe.test.richfaces;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Class for testing all RichFaces components
 * 
 * @author dsakovich@exadel.com
 * 
 */

public class RichFacesAllTest {

    public static Test suite() {
	TestSuite suite = new TestSuite("Tests for Vpe RichFaces components");
	// $JUnit-BEGIN$
	suite.addTestSuite(RichFacesComponentTest.class);
	// $JUnit-END$
	return suite;

    }

}
