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

import org.eclipse.core.resources.IFile;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author mareshkau
 *	junit http://jira.jboss.org/jira/browse/JBIDE-2434
 */
public class JBIDE2434Test extends VpeTest{

	private static final String IMPORT_PROJECT_NAME = "jsfTest"; //$NON-NLS-1$
	
	public JBIDE2434Test(String name) {
		super(name);
	}
	
	public void testOpenAndCloPageWithCycleFacelets() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("JBIDE/2434/FaceletForm.xhtml",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	
	
}
