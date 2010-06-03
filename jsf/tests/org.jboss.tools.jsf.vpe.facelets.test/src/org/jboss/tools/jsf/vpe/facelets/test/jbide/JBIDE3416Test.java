/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.facelets.test.jbide;

import org.jboss.tools.jsf.vpe.facelets.test.FaceletsAllTests;
import org.jboss.tools.vpe.ui.test.ComponentContentTest;

public class JBIDE3416Test extends ComponentContentTest {

    public JBIDE3416Test(String name) {
	super(name);
    }

    public void _testJBIDE3416() throws Throwable {	
	performContentTest("JBIDE/3416/jbide3416.xhtml"); //$NON-NLS-1$
    }
    
    @Override
    protected String getTestProjectName() {
	return FaceletsAllTests.IMPORT_PROJECT_NAME;
    }

}
