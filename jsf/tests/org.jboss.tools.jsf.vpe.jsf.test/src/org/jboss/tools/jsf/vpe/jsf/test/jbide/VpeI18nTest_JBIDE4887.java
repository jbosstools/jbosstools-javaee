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
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.ui.test.ComponentContentTest;

/**
 * Tests for JIRA issue JBIDE-4887: Internationalization does not work in VPE 
 * (https://jira.jboss.org/jira/browse/JBIDE-4887 )
 * 
 * @author yradtsevich
 */
public class VpeI18nTest_JBIDE4887 extends ComponentContentTest {

	public VpeI18nTest_JBIDE4887(String name) {
		super(name);
		setCheckWarning(false);
	}

	/**
	 * Tests if the default-locale described in faces-config.xml is
	 * taken into account during VPE rendering.
	 */
	public void testJsfDefaultLocale() throws Throwable {
		performContentTest("jsfDefaultLocale.jsp"); //$NON-NLS-1$
	}

	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_I18N_PROJECT_NAME;
	}
}
