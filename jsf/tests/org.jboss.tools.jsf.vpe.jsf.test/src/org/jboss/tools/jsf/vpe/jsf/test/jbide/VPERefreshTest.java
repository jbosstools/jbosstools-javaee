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
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMElement;

/**
 * 
 * Test case for https://jira.jboss.org/jira/browse/JBIDE-4816
 * 
 * @author mareshkau
 */
public class VPERefreshTest extends VpeTest{

	public VPERefreshTest(String name) {
		super(name);
	}
	
	public void testVisualContentInVpeVeforeRefreshAndAfter() throws Throwable {
		VpeController vpeController =
		openInVpe(JsfAllTests.IMPORT_PROJECT_NAME, "JBIDE/4816/testVisualContentBeforeRefreshAndAfter.html");//$NON-NLS-1$
		vpeController.getSourceEditor().getTextViewer().setSelectedRange(0, 0);
		nsIDOMElement bodyBeforeRefresh = vpeController.getVisualBuilder().getContentArea();
		long beforeRefreshChields = bodyBeforeRefresh.getChildNodes().getLength();
		vpeController.visualRefresh();
		TestUtil.waitForIdle();
		nsIDOMElement bodyAfterRefresh = vpeController.getVisualBuilder().getContentArea();
		assertEquals("Number of child nodes before and after refresh should be the same",beforeRefreshChields, bodyAfterRefresh.getChildNodes().getLength()); //$NON-NLS-1$
	}

}
