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
import org.jboss.tools.vpe.editor.VpeController;
import org.mozilla.interfaces.nsIDOMElement;


/**
 * @author mareshkau
 * Test case for https://jira.jboss.org/jira/browse/JBIDE-3482
 */
public class JBIDE3482Test extends ComponentContentTest{

	public JBIDE3482Test(String name) {
		super(name);
	}
	
	public void testJBIDE3482() throws Throwable {	
		performContentTest("JBIDE/3482/jbide3482.xhtml"); //$NON-NLS-1$
	}
	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_PROJECT_NAME;
	}
	@Override
	protected nsIDOMElement findElementById(VpeController controller,
			String elementId){
	return	controller.getXulRunnerEditor().getDOMDocument().getElementById(elementId);
	}

}
