/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.seam3.bot.test.uiutils;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.AssignableBeansDialog;
/**
 * 
 * @author jjankovi
 *
 */
public class AssignableBeansDialogExt extends AssignableBeansDialog {

	private final String ELIMINATED_DEFAULT_BEAN = "Eliminated @DefaultBean";
	
	public AssignableBeansDialogExt(SWTBotShell shell) {
		super(shell);	
		showDefaultBeans();
	}
	
	public AssignableBeansDialog hideDefaultBeans() {
		getTreeItem(ELIMINATED_DEFAULT_BEAN).uncheck();
		return this;
	}
	
	public AssignableBeansDialog showDefaultBeans() {
		getTreeItem(ELIMINATED_DEFAULT_BEAN).check();
		return this;
	}
	
}
