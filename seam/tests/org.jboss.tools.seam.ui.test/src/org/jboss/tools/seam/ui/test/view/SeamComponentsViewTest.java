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

package org.jboss.tools.seam.ui.test.view;

import junit.framework.TestCase;

import org.eclipse.ui.IWorkbenchPage;
import org.jboss.tools.jst.web.ui.RedHat4WebPerspectiveFactory;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * 
 * @author eskimo
 *
 */
public class SeamComponentsViewTest extends TestCase {

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		WorkbenchUtils.getWorkbench().showPerspective(
				RedHat4WebPerspectiveFactory.PERSPECTIVE_ID,
				WorkbenchUtils.getWorkbench().getActiveWorkbenchWindow());
		}

	/**
	 * 
	 */
	public void testSeamComponentsViewIsShowedOnPerspective() {
		IWorkbenchPage page  = WorkbenchUtils.getWorkbenchActivePage();
		page.findView(ISeamUiConstants.SEAM_COMPONENTS_VIEW_ID);
	}
	
}
