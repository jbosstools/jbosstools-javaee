/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.base.test.ComponentContentTest;

/**
 * @author mareshkau
 *
 */
public class JBIDE3376Test extends ComponentContentTest{

	public JBIDE3376Test(String name) {
		super(name);
	}

	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_PROJECT_NAME;
	}
	
	public void testJBIDE3144Test2() throws Throwable {
		performContentTest("JBIDE/3376/jbide3376.jsp"); //$NON-NLS-1$
	}

}
