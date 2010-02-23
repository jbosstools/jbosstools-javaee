/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
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
 * https://jira.jboss.org/jira/browse/JBIDE-5908
 * 
 * @author mareshkau
 * 
 */
public class RenderFacetAndInsertChildrenTest extends ComponentContentTest{

	public RenderFacetAndInsertChildrenTest(String name) {
		super(name);
	}
	public void testRenderFacetAndInsertChildren() throws Throwable {	
		performContentTest("JBIDE/5908/welcome.xhtml"); //$NON-NLS-1$
	}
	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_JSF_20_PROJECT_NAME;
	}

}
