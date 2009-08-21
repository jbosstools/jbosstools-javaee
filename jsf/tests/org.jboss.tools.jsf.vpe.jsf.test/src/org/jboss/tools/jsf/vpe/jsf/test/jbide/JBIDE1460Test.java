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
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.ui.test.ComponentContentTest;

/**
 * Test JBIDE-1460
 * 
 * @author dsakovich@exadel.com
 * @author yradtsevich
 * 
 */
public class JBIDE1460Test extends ComponentContentTest {
    public JBIDE1460Test(String name) {
    	super(name);
    }

    // test method for JBIDE 1460
    public void testJBIDE_1460() throws Throwable {
    	performContentTest( "JBIDE/1460/JBIDE-1460.xhtml"); //$NON-NLS-1$
    }

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.ui.test.ComponentContentTest#getTestProjectName()
	 */
	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_PROJECT_NAME;
	}

}
