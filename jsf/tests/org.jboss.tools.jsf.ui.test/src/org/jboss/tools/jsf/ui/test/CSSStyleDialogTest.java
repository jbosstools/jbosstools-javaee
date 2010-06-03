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

package org.jboss.tools.jsf.ui.test;

import junit.framework.TestCase;

import org.jboss.tools.jst.jsp.outline.cssdialog.CSSStyleDialog;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author Sergey Dzmitrovich
 *
 */
public class CSSStyleDialogTest extends TestCase {

	private static String CSS_STYLE = "color:red;size:10px;"; //$NON-NLS-1$

	public void testStyleDialog() {
		CSSStyleDialog dialog = null;
		try {
			dialog = new CSSStyleDialog(WorkbenchUtils
					.getActiveShell(), CSS_STYLE);
			dialog.setBlockOnOpen(false);
			dialog.open();
		} finally {
			if(dialog !=null) {	
				dialog.close();
			}
		}
	}
}
